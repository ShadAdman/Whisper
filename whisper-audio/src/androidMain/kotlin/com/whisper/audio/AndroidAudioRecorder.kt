package com.whisper.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.whisper.core.model.AudioFrame
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class AndroidAudioRecorder : AudioRecorder {
    private val _samples = MutableSharedFlow<AudioFrame>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val samples: Flow<AudioFrame> = _samples
    
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val sampleRate = 48000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    @SuppressLint("MissingPermission")
    override suspend fun start() {
        if (audioRecord != null) return

        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        if (minBufferSize <= 0) {
            println("Error: Invalid minBufferSize $minBufferSize")
            return
        }
        
        val actualBufferSize = maxOf(minBufferSize, 1024 * 2)

        audioRecord = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(audioFormat)
                    .setSampleRate(sampleRate)
                    .setChannelMask(channelConfig)
                    .build()
            )
            .setBufferSizeInBytes(actualBufferSize)
            .build()

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            println("Error: AudioRecord initialization failed")
            audioRecord = null
            return
        }

        audioRecord?.startRecording()

        recordingJob = scope.launch {
            val shortBuffer = ShortArray(2048)
            val floatBuffer = FloatArray(2048)
            while (isActive && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val read = audioRecord?.read(shortBuffer, 0, shortBuffer.size) ?: -1
                if (read > 0) {
                    for (i in 0 until read) {
                        floatBuffer[i] = shortBuffer[i] / 32768f
                    }
                    _samples.emit(
                        AudioFrame(
                            samples = floatBuffer.copyOf(read),
                            sampleRate = sampleRate,
                            channels = 1,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    override suspend fun stop() {
        recordingJob?.cancelAndJoin()
        recordingJob = null
        
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}
