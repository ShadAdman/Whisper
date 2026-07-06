package com.whisper.audio

interface AudioPlayer {
    suspend fun play(samples: FloatArray)
    suspend fun stop()
}

expect fun createAudioPlayer(): AudioPlayer
