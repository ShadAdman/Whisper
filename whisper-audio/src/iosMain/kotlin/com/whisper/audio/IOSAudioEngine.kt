package com.whisper.audio

class IOSAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = object : AudioRecorder {
        override suspend fun start() { TODO("Not yet implemented") }
        override suspend fun stop() { TODO("Not yet implemented") }
    }
    
    override val player: AudioPlayer = object : AudioPlayer {
        override suspend fun play(samples: FloatArray) { TODO("Not yet implemented") }
        override suspend fun stop() { TODO("Not yet implemented") }
    }

    override suspend fun setup() { TODO("Not yet implemented") }
    override suspend fun release() { TODO("Not yet implemented") }
}
