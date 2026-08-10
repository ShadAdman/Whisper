package com.whisper.audio

class DesktopAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = createAudioRecorder()
    override val player: AudioPlayer = createAudioPlayer()

    override suspend fun setup() {
        // Resources are lazily initialized in recorder/player or during start/play
    }

    override suspend fun release() {
        recorder.stop()
        player.stop()
    }
}

actual fun createAudioEngine(): AudioEngine = DesktopAudioEngine()
