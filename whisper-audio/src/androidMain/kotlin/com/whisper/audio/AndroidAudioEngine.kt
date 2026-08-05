package com.whisper.audio

class AndroidAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = AndroidAudioRecorder()
    override val player: AudioPlayer = AndroidAudioPlayer(requireNotNull(ContextHolder.context) { "Context not initialized. Make sure AudioInitializer is running." })

    override suspend fun setup() {
        // TODO: Request permissions, initialize engine
    }

    override suspend fun release() {
        // TODO: Release resources
    }
}

actual fun createAudioEngine(): AudioEngine = AndroidAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = AndroidAudioPlayer(requireNotNull(ContextHolder.context) { "Context not initialized" })

actual fun createAudioRecorder(): AudioRecorder = AndroidAudioRecorder()
