package com.whisper.audio

import com.whisper.core.model.AudioFrame
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import alsa.*
import com.whisper.core.util.WLogger

@OptIn(ExperimentalForeignApi::class)
class LinuxAudioPlayer : AudioPlayer {
    private val TAG = "LinuxAudioPlayer"
    private var pcmHandle: CPointer<snd_pcm_t>? = null

    private fun setupPcm() {
        if (pcmHandle != null) return

        val pcmName = "default"
        val rate = 48000
        val channels = 1

        memScoped {
            val handlePtr = alloc<CPointerVar<snd_pcm_t>>()
            val openRes = snd_pcm_open(handlePtr.ptr, pcmName, SND_PCM_STREAM_PLAYBACK, 0)
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
            snd_pcm_prepare(pcmHandle)
            WLogger.i(TAG, "Player started at ${rateVar.value} Hz, $channels channels")
        }
    }

    override suspend fun play(frame: AudioFrame) {
        withContext(Dispatchers.Default) {
            setupPcm()
            val shortBuffer = ShortArray(frame.samples.size)
            for (i in frame.samples.indices) {
                shortBuffer[i] = (frame.samples[i] * 32767f).toInt().coerceIn(-32768, 32767).toShort()
            }

            shortBuffer.usePinned { pinned ->
                val written = snd_pcm_writei(pcmHandle, pinned.addressOf(0), frame.samples.size.toULong())
                if (written < 0) {
                    snd_pcm_prepare(pcmHandle)
                    snd_pcm_writei(pcmHandle, pinned.addressOf(0), frame.samples.size.toULong())
                }
            }
        }
    }

    override suspend fun stop() {
        pcmHandle?.let {
            snd_pcm_drop(it)
            snd_pcm_close(it)
        }
        pcmHandle = null
    }
}
