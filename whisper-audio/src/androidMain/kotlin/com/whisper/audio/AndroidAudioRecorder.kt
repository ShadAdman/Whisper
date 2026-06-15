package com.whisper.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*

class AndroidAudioRecorder : AudioRecorder {
    private var listener: ((FloatArray) -> Unit)? = null
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val sampleRate = 48000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_FLOAT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    override fun setListener(listener: (FloatArray) -> Unit) {
        this.listener = listener
    }

    @SuppressLint("MissingPermission")
    override suspend fun start() {
        if (audioRecord != null) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecord?.startRecording()

        recordingJob = scope.launch {
            val buffer = FloatArray(bufferSize / 4) // Float is 4 bytes
            while (isActive && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val read = audioRecord?.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING) ?: -1
                if (read > 0) {
                    listener?.invoke(buffer.copyOf(read))
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
