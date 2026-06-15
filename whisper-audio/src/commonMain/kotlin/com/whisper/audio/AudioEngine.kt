package com.whisper.audio

interface AudioEngine {
    val recorder: AudioRecorder
    val player: AudioPlayer
    
    suspend fun setup()
    suspend fun release()
}

expect fun createAudioEngine(): AudioEngine
