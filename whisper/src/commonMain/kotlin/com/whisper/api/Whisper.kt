package com.whisper.api

import com.whisper.audio.AudioEngine
import com.whisper.audio.createAudioEngine
import com.whisper.config.WhisperConfig
import com.whisper.core.model.FrequencyDetection
import com.whisper.dsp.detector.DefaultFrequencyDetector
import com.whisper.dsp.fft.createFFTProcessor
import com.whisper.dsp.generator.SignalGenerator
import com.whisper.dsp.generator.createSignalGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object Whisper {
    private val mutex = Mutex()
    private var engine: AudioEngine? = null
    private var config: WhisperConfig = WhisperConfig()

    val detectedFrequency: Flow<FrequencyDetection> = flow {
        val currentEngine = getOrInitializeEngine()
        val processor = createFFTProcessor(2048)
        val detector = DefaultFrequencyDetector(processor)
        try {
            currentEngine.recorder.samples.collect { samples ->
                emit(detector.detectFrequency(samples))
            }
        } finally {
            processor.release()
        }
    }

    suspend fun startListening() = mutex.withLock {
        val currentEngine = getOrInitializeEngine()
        currentEngine.recorder.start()
    }

    suspend fun stopListening() = mutex.withLock {
        engine?.recorder?.stop()
    }

    suspend fun playTestTone() = mutex.withLock {
        val currentEngine = getOrInitializeEngine()
        val generator = createSignalGenerator()
        val samples = generator.generateTone(19000f, 1000)
        println("Samples Has been generated and post to player")
        currentEngine.player.play(samples)
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
