package com.whisper.audio

class DesktopAudioRecorder : AudioRecorder {
    private var listener: ((FloatArray) -> Unit)? = null

    override fun setListener(listener: (FloatArray) -> Unit) {
        this.listener = listener
    }
    override suspend fun start() {
        // TODO: Implement using Java Sound API or PortAudio
    }

    override suspend fun stop() {
        // TODO: Implement
    }
}
