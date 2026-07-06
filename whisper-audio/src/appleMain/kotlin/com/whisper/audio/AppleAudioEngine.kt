package com.whisper.audio

class AppleAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = AppleAudioRecorder()
    override val player: AudioPlayer = AppleAudioPlayer()

    override suspend fun setup() {
        // TODO: Configure AVAudioSession (iOS), initialize engine
    }

    override suspend fun release() {
        // TODO: Release resources
    }
}

actual fun createAudioEngine(): AudioEngine = AppleAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = AppleAudioPlayer()

actual fun createAudioRecorder(): AudioRecorder = AppleAudioRecorder()
