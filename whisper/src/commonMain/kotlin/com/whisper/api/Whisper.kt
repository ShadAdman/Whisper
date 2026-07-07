package com.whisper.api

import com.whisper.audio.AudioEngine
import com.whisper.audio.createAudioEngine
import com.whisper.config.WhisperConfig
import com.whisper.core.model.AudioFrame
import com.whisper.core.model.FrequencyDetection
import com.whisper.dsp.detector.PeakDetectorStage
import com.whisper.dsp.fft.createFFTProcessor
import com.whisper.dsp.generator.SignalGenerator
import com.whisper.dsp.generator.createSignalGenerator
import com.whisper.dsp.pipeline.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object Whisper {
    private val mutex = Mutex()
    private var engine: AudioEngine? = null
    private var config: WhisperConfig = WhisperConfig()

    private val pipeline = DSPPipeline(
        listOf(
            WindowStage(),
            BandPassStage(),
            GainStage()
        )
    )

    val detectedFrequency: Flow<FrequencyDetection> = flow {
        val currentEngine = getOrInitializeEngine()
        val processor = createFFTProcessor(2048)
        val peakDetector = PeakDetectorStage()
        try {
            currentEngine.recorder.samples
                .map { frame -> pipeline.process(frame) }
                .mapNotNull { processedFrame ->
                    val spectrum = processor.process(processedFrame.samples)
                    peakDetector.detect(spectrum, processedFrame.timestamp)
                }
                .collect { detection ->
                    emit(detection)
                }
        } finally {
            processor.release()
            pipeline.release()
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
        currentEngine.player.play(
            AudioFrame(
                samples = samples,
                sampleRate = 48000,
                channels = 1,
                timestamp = 0 // In play mode timestamp might be less critical or current time
            )
        )
    }

    suspend fun transmit(data: ByteArray) = mutex.withLock {
        val currentEngine = getOrInitializeEngine()
        // TODO: Use ModemEncoder to convert data to samples
        val samples = FloatArray(0) 
        currentEngine.player.play(
            AudioFrame(
                samples = samples,
                sampleRate = 48000,
                channels = 1,
                timestamp = 0
            )
        )
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
