package com.whisper.audio

interface AudioRecorder {
    suspend fun start()
    suspend fun stop()
}
