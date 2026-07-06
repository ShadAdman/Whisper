package com.whisper.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class LinuxAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = object : AudioRecorder {
        override val samples: Flow<FloatArray> = emptyFlow()
        override suspend fun start() {}
        override suspend fun stop() {}
    }
    override val player: AudioPlayer = object : AudioPlayer {
        override suspend fun play(samples: FloatArray) {}
        override suspend fun stop() {}
    }

    override suspend fun setup() {}
    override suspend fun release() {}
}

actual fun createAudioEngine(): AudioEngine = LinuxAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = (LinuxAudioEngine()).player

actual fun createAudioRecorder(): AudioRecorder = (LinuxAudioEngine()).recorder
