package com.whisper.audio

class AndroidAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = AndroidAudioRecorder()
    override val player: AudioPlayer = AndroidAudioPlayer()

    override suspend fun setup() {
        // TODO: Request permissions, initialize engine
    }

    override suspend fun release() {
        // TODO: Release resources
    }
}

actual fun createAudioEngine(): AudioEngine = AndroidAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = AndroidAudioPlayer()

actual fun createAudioRecorder(): AudioRecorder = AndroidAudioRecorder()
