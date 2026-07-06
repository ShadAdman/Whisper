package com.whisper.audio

class DesktopAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = DesktopAudioRecorder()
    override val player: AudioPlayer = DesktopAudioPlayer()

    override suspend fun setup() {
        // TODO: Initialize target data line and source data line
    }

    override suspend fun release() {
        // TODO: Release resources
    }
}

actual fun createAudioEngine(): AudioEngine = DesktopAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = DesktopAudioPlayer()

actual fun createAudioRecorder(): AudioRecorder = DesktopAudioRecorder()
