package com.whisper.audio

import com.whisper.core.model.AudioFrame
import kotlinx.coroutines.flow.Flow

interface AudioRecorder {
    val samples: Flow<AudioFrame>
    suspend fun start()
    suspend fun stop()
}

expect fun createAudioRecorder(): AudioRecorder
