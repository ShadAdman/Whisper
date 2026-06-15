package com.whisper.audio

interface AudioRecorder {
    fun setListener(listener: (FloatArray) -> Unit)
    suspend fun start()
    suspend fun stop()
}
