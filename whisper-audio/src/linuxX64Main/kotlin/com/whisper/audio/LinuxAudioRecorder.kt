package com.whisper.audio

import com.whisper.core.model.AudioFrame
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import alsa.*
import kotlinx.datetime.Clock
import com.whisper.core.util.WLogger

@OptIn(ExperimentalForeignApi::class)
class LinuxAudioRecorder : AudioRecorder {
    private val TAG = "LinuxAudioRecorder"
    private val _samples = MutableSharedFlow<AudioFrame>(extraBufferCapacity = 64)
    override val samples: SharedFlow<AudioFrame> = _samples.asSharedFlow()

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var pcmHandle: CPointer<snd_pcm_t>? = null

    override suspend fun start() {
        if (job != null) return

        val pcmName = "default"
        val rate = 48000
        val channels = 1
        val frames = 1024

        memScoped {
            val handlePtr = alloc<CPointerVar<snd_pcm_t>>()
            val openRes = snd_pcm_open(handlePtr.ptr, pcmName, SND_PCM_STREAM_CAPTURE, 0)
            if (openRes < 0) {
                WLogger.e(TAG, "Cannot open PCM device $pcmName: $openRes")
                throw IllegalStateException("Cannot open PCM device $pcmName: $openRes")
            }
            pcmHandle = handlePtr.value

            val paramsPtr = alloc<CPointerVar<snd_pcm_hw_params_t>>()
            snd_pcm_hw_params_malloc(paramsPtr.ptr)
            val params = paramsPtr.value!!

            snd_pcm_hw_params_any(pcmHandle, params)
            snd_pcm_hw_params_set_access(pcmHandle, params, SND_PCM_ACCESS_RW_INTERLEAVED)
            snd_pcm_hw_params_set_format(pcmHandle, params, SND_PCM_FORMAT_S16_LE)
            snd_pcm_hw_params_set_channels(pcmHandle, params, channels.toUInt())
            
            val rateVar = alloc<UIntVar>().apply { value = rate.toUInt() }
            snd_pcm_hw_params_set_rate_near(pcmHandle, params, rateVar.ptr, null)
            
            val hwRes = snd_pcm_hw_params(pcmHandle, params)
            if (hwRes < 0) {
                snd_pcm_hw_params_free(params)
                WLogger.e(TAG, "Cannot set hardware parameters: $hwRes")
                throw IllegalStateException("Cannot set hardware parameters: $hwRes")
            }
            snd_pcm_hw_params_free(params)
            WLogger.i(TAG, "Recorder started at ${rateVar.value} Hz, $channels channels")
        }

        job = scope.launch(Dispatchers.Default) {
            val buffer = ShortArray(frames)
            val floatBuffer = FloatArray(frames)
            
            while (isActive) {
                buffer.usePinned { pinned ->
                    val read = snd_pcm_readi(pcmHandle, pinned.addressOf(0), frames.toULong())
                    if (read < 0) {
                        val errorMsg = "ALSA Error $read"
                        WLogger.e(TAG, "PCM read error: $errorMsg. Recovering...")
                        snd_pcm_prepare(pcmHandle)
                    } else if (read > 0) {
                        for (i in 0 until read.toInt()) {
                            floatBuffer[i] = buffer[i] / 32768f
                        }
                        _samples.emit(
                            AudioFrame(
                                samples = floatBuffer.copyOf(read.toInt()),
                                sampleRate = rate,
                                channels = channels,
                                timestamp = Clock.System.now().toEpochMilliseconds()
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun stop() {
        job?.cancelAndJoin()
        job = null
        pcmHandle?.let {
            snd_pcm_close(it)
        }
        pcmHandle = null
    }
}
