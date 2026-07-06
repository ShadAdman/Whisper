package com.whisper.audio

import com.whisper.core.model.AudioFrame

interface AudioPlayer {
    suspend fun play(frame: AudioFrame)
    suspend fun stop()
}

expect fun createAudioPlayer(): AudioPlayer
