package com.whisper.audio

class IOSAudioRecorder : AudioRecorder {
    private var listener: ((FloatArray) -> Unit)? = null

    override fun setListener(listener: (FloatArray) -> Unit) {
        this.listener = listener
    }
    override suspend fun start() {
        // TODO: Implement using AVAudioEngine or AudioUnit
    }

    override suspend fun stop() {
        // TODO: Implement
    }
}
