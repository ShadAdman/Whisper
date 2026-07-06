package com.whisper.audio

import kotlinx.coroutines.flow.Flow

interface AudioRecorder {
    val samples: Flow<FloatArray>
    suspend fun start()
    suspend fun stop()
}

expect fun createAudioRecorder(): AudioRecorder
