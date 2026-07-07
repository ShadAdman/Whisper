package com.whisper.api

import com.whisper.audio.AudioEngine
import com.whisper.audio.createAudioEngine
import com.whisper.config.WhisperConfig
import com.whisper.core.model.AudioFrame
import com.whisper.core.model.CarrierEvent
import com.whisper.core.model.FrequencyDetection
import com.whisper.dsp.detector.CarrierDetector
import com.whisper.dsp.detector.PeakDetectorConfig
import com.whisper.dsp.detector.PeakDetectorStage
import com.whisper.dsp.fft.createFFTProcessor
import com.whisper.dsp.generator.SignalGenerator
import com.whisper.dsp.generator.createSignalGenerator
import com.whisper.dsp.modem.*
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

    val decodedBits: Flow<Int> = flow {
        val currentEngine = getOrInitializeEngine()
        val processor = createFFTProcessor(2048)
        val peakDetector = PeakDetectorStage(
            PeakDetectorConfig(
                minimumMagnitude = 0.1f,
                requiredStableFrames = 1,
                allowDuplicates = true
            )
        )
        val fskDecoder = FSKDecoder()
        
        try {
            currentEngine.recorder.samples
                .map { frame -> pipeline.process(frame) }
                .mapNotNull { processedFrame ->
                    val spectrum = processor.process(processedFrame.samples)
                    peakDetector.detect(spectrum, processedFrame.timestamp)
                }
                .map { detection ->
                    fskDecoder.decode(detection)
                }
                .collect { bit ->
                    emit(bit)
                }
        } finally {
            processor.release()
        }
    }

    val receivedData: Flow<ByteArray> = flow {
        val bitDecoder = DefaultBitDecoder()
        val bitStreamCollector = BitStreamCollector()
        
        var currentBit: Int? = null
        var bitFrames = 0
        val framesPerSymbol = 5 // ~42ms * 5 = 210ms (matching 200ms symbol)

        decodedBits.collect { bit ->
            if (bit == currentBit) {
                bitFrames++
            } else {
                if (currentBit != null && currentBit != -1) { // -1 could be "lost" but here fskDecoder returns null for lost
                    val numBits = (bitFrames.toFloat() / framesPerSymbol + 0.5f).toInt()
                    repeat(numBits) { bitStreamCollector.addBit(currentBit!!) }
                    
                    while (bitStreamCollector.getBits().size >= 8) {
                        val allBits = bitStreamCollector.getBits()
                        val bytes = bitDecoder.decode(allBits.take(8))
                        emit(bytes)
                        bitStreamCollector.consume(8)
                    }
                }
                currentBit = bit
                bitFrames = 1
            }
        }
    }

    val carrierEvents: Flow<CarrierEvent> = flow {
        val currentEngine = getOrInitializeEngine()
        val processor = createFFTProcessor(2048)
        val peakDetector = PeakDetectorStage()
        val carrierDetector = CarrierDetector()
        try {
            currentEngine.recorder.samples
                .map { frame -> pipeline.process(frame) }
                .mapNotNull { processedFrame ->
                    val spectrum = processor.process(processedFrame.samples)
                    peakDetector.detect(spectrum, processedFrame.timestamp)
                }
                .mapNotNull { detection ->
                    carrierDetector.process(detection)
                }
                .collect { event ->
                    emit(event)
                }
        } finally {
            processor.release()
        }
    }

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
        val encoder = FSKEncoder()
        val samples = encoder.encode(data)
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
