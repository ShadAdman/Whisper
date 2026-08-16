package com.whisper.audio

class LinuxAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = LinuxAudioRecorder()
    override val player: AudioPlayer = LinuxAudioPlayer()

    override suspend fun setup() {
        // Initialization is handled in start/play
    }

    override suspend fun release() {
        recorder.stop()
        player.stop()
    }
}

actual fun createAudioEngine(): AudioEngine = LinuxAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = LinuxAudioPlayer()

actual fun createAudioRecorder(): AudioRecorder = LinuxAudioRecorder()
