package com.whisper.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.whisper.core.model.AudioFrame
import com.whisper.core.util.WLogger
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

        // Try 48000 then 44100
        val rates = intArrayOf(48000, 44100)
        var successfulRate = 0
        
        for (rate in rates) {
            val minBufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat)
            if (minBufferSize <= 0) continue
            
            val actualBufferSize = maxOf(minBufferSize, 1024 * 2)

            try {
                audioRecord = AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(audioFormat)
                            .setSampleRate(rate)
                            .setChannelMask(channelConfig)
                            .build()
                    )
                    .setBufferSizeInBytes(actualBufferSize)
                    .build()
            } catch (e: Exception) {
                WLogger.e("AndroidAudioRecorder", "Failed to create AudioRecord with VOICE_RECOGNITION: ${e.message}")
                try {
                    audioRecord = AudioRecord.Builder()
                        .setAudioSource(MediaRecorder.AudioSource.MIC)
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(audioFormat)
                                .setSampleRate(rate)
                                .setChannelMask(channelConfig)
                                .build()
                        )
                        .setBufferSizeInBytes(actualBufferSize)
                        .build()
                } catch (e2: Exception) {
                    WLogger.e("AndroidAudioRecorder", "Failed to create AudioRecord with MIC: ${e2.message}")
                    continue
                }
            }

            if (audioRecord?.state == AudioRecord.STATE_INITIALIZED) {
                successfulRate = rate
                break
            } else {
                audioRecord?.release()
                audioRecord = null
            }
        }

        if (audioRecord == null) {
            println("Error: Failed to initialize AudioRecord with any supported rate")
            return
        }

        val finalRate = successfulRate
        audioRecord?.startRecording()
        WLogger.i("AndroidAudioRecorder", "Recording started successfully at $finalRate Hz using source ${audioRecord?.audioSource}")

        recordingJob = scope.launch {
            val shortBuffer = ShortArray(2048)
            val floatBuffer = FloatArray(2048)
            while (isActive && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val read = audioRecord?.read(shortBuffer, 0, shortBuffer.size) ?: -1
                if (read > 0) {
                    var maxVal = 0f
                    for (i in 0 until read) {
                        floatBuffer[i] = shortBuffer[i] / 32768f
                        if (kotlin.math.abs(floatBuffer[i]) > maxVal) maxVal = kotlin.math.abs(floatBuffer[i])
                    }
                    if (maxVal > 0.01f) {
                        WLogger.d("AndroidAudioRecorder", "Read $read samples, max amp: $maxVal")
                    }
                    _samples.emit(
                        AudioFrame(
                            samples = floatBuffer.copyOf(read),
                            sampleRate = finalRate,
                            channels = 1,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } else if (read < 0) {
                    WLogger.e("AndroidAudioRecorder", "Error reading from AudioRecord: $read")
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
