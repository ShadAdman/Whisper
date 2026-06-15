package com.whisper.api

import com.whisper.audio.AudioEngine
import com.whisper.audio.createAudioEngine
import com.whisper.config.WhisperConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object Whisper {
    private val mutex = Mutex()
    private var engine: AudioEngine? = null
    private var config: WhisperConfig = WhisperConfig()

    suspend fun startListening() = mutex.withLock {
        val currentEngine = getOrInitializeEngine()
        currentEngine.recorder.start()
    }

    suspend fun stopListening() = mutex.withLock {
        engine?.recorder?.stop()
    }

    suspend fun transmit(data: ByteArray) = mutex.withLock {
        val currentEngine = getOrInitializeEngine()
        // TODO: Use ModemEncoder to convert data to samples
        val samples = FloatArray(0) 
        currentEngine.player.play(samples)
    }
    
    fun configure(config: WhisperConfig) {
        this.config = config
    }

    private suspend fun getOrInitializeEngine(): AudioEngine {
        return engine ?: createAudioEngine().also {
            it.setup()
            engine = it
        }
    }
}
