package com.whisper.audio

class AppleAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = AppleAudioRecorder()
    override val player: AudioPlayer = AppleAudioPlayer()

    override suspend fun setup() {
        // macOS does not require AVAudioSession setup
    }

    override suspend fun release() {
        // No-op for now
    }
}

actual fun createAudioEngine(): AudioEngine = AppleAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = AppleAudioPlayer()

actual fun createAudioRecorder(): AudioRecorder = AppleAudioRecorder()
