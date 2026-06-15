package com.whisper.audio

class AndroidAudioRecorder : AudioRecorder {
    private var listener: ((FloatArray) -> Unit)? = null

    override fun setListener(listener: (FloatArray) -> Unit) {
        this.listener = listener
    }
    override suspend fun start() {
        // TODO: Implement using AudioRecord or Oboe
    }

    override suspend fun stop() {
        // TODO: Implement
    }
}
