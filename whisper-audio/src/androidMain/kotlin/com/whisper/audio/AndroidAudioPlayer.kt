package com.whisper.audio

class AndroidAudioPlayer : AudioPlayer {
    override suspend fun play(samples: FloatArray) {
        // TODO: Implement using AudioTrack or Oboe
    }

    override suspend fun stop() {
        // TODO: Implement
    }
}
