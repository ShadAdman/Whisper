package com.whisper.audio

class IOSAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = IOSAudioRecorder()
    override val player: AudioPlayer = IOSAudioPlayer()

    override suspend fun setup() {
        // TODO: Configure AVAudioSession, initialize engine
    }

    override suspend fun release() {
        // TODO: Release resources
    }
}

actual fun createAudioEngine(): AudioEngine = IOSAudioEngine()
