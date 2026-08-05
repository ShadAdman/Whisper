package com.whisper.api

import com.whisper.audio.AudioEngine
import com.whisper.audio.createAudioEngine
import com.whisper.config.WhisperConfig
import com.whisper.core.model.AudioFrame
import com.whisper.core.model.CarrierEvent
import com.whisper.core.model.FrequencyDetection
import com.whisper.core.packet.*
import com.whisper.core.util.WLogger
import com.whisper.dsp.detector.CarrierDetector
import com.whisper.dsp.detector.PeakDetectorConfig
import com.whisper.dsp.detector.PeakDetectorStage
import com.whisper.dsp.fft.createFFTProcessor
import com.whisper.dsp.generator.SignalGenerator
import com.whisper.dsp.generator.createSignalGenerator
import com.whisper.dsp.modem.*
import com.whisper.dsp.pipeline.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object Whisper {
    private val engineMutex = Mutex()
    private val recorderMutex = Mutex()
    private val playerMutex = Mutex()
    
    private var engine: AudioEngine? = null
    private var config: WhisperConfig = WhisperConfig()

    private val pipeline = DSPPipeline(
        listOf(
            WindowStage(),
            BandPassStage(),
            GainStage()
        )
    )

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _rawDetections = MutableSharedFlow<FrequencyDetection>()
    val rawDetections: Flow<FrequencyDetection> = _rawDetections

    private var detectionJob: Job? = null

    val detectedFrequency: Flow<FrequencyDetection> = rawDetections
        .filter { it.frequency > 0 }

    val decodedBits: Flow<Int> = flow {
        val fskDecoder = FSKDecoder()
        rawDetections.collect { detection ->
            emit(fskDecoder.decode(detection))
        }
    }

    val packetResults: Flow<PacketResult> = flow {
        val bitDecoder = DefaultBitDecoder()
        val bitStreamCollector = BitStreamCollector()
        val synchronizer = PacketSynchronizer(fecConfig = config.fecConfig)
        
        var currentBit: Int? = null
        var bitFrames = 0
        val framesPerSymbol = 5 // ~42ms * 5 = 210ms (matching 200ms symbol)

        decodedBits.collect { bit ->
            if (bit == currentBit) {
                bitFrames++
            } else {
                if (currentBit != null && currentBit != -1) {
                    val numBits = (bitFrames.toFloat() / framesPerSymbol + 0.5f).toInt()
                    repeat(numBits) { bitStreamCollector.addBit(currentBit!!) }
                    
                    while (bitStreamCollector.getBits().size >= 8) {
                        val allBits = bitStreamCollector.getBits()
                        val bytes = bitDecoder.decode(allBits.take(8))
                        val result = synchronizer.processByte(bytes[0])
                        if (result != null) {
                            emit(result)
                        }
                        bitStreamCollector.consume(8)
                    }
                }
                currentBit = bit
                bitFrames = 1
            }
        }
    }

    val receivedPackets: Flow<WhisperPacket> = packetResults
        .filterIsInstance<ValidPacket>()
        .map { it.packet }

    val receivedData: Flow<ByteArray> = receivedPackets.map { it.payload }

    val carrierEvents: Flow<CarrierEvent> = flow {
        val carrierDetector = CarrierDetector()
        rawDetections.collect { detection ->
            val event = carrierDetector.process(detection)
            if (event != null) {
                WLogger.i("Whisper", "Carrier event emitted: $event")
                emit(event)
            }
        }
    }

    suspend fun startListening() = recorderMutex.withLock {
        WLogger.i("Whisper", "startListening called")
        val currentEngine = getOrInitializeEngine()
        currentEngine.recorder.start()

        if (detectionJob == null) {
            detectionJob = scope.launch {
                WLogger.i("Whisper", "Starting detection loop")
                val processor = createFFTProcessor(2048)
                val peakDetector = PeakDetectorStage(
                    PeakDetectorConfig(
                        minimumMagnitude = 0.001f, // Extremely sensitive for testing
                        requiredStableFrames = 1,
                        allowDuplicates = true
                    )
                )
                try {
                    currentEngine.recorder.samples
                        .map { frame -> pipeline.process(frame) }
                        .mapNotNull { processedFrame ->
                            val spectrum = processor.process(processedFrame.samples, processedFrame.sampleRate.toFloat())
                            peakDetector.detect(spectrum, processedFrame.timestamp)
                        }
                        .collect { detection ->
                            WLogger.d("Whisper", "Peak: ${detection.frequency.toInt()}Hz, mag: ${detection.magnitude}")
                            _rawDetections.emit(detection)
                        }
                } catch (e: Exception) {
                    WLogger.e("Whisper", "Detection loop error: ${e.message}")
                } finally {
                    WLogger.i("Whisper", "Releasing processor")
                    processor.release()
                }
            }
        }
    }

    suspend fun stopListening() = recorderMutex.withLock {
        WLogger.i("Whisper", "stopListening called")
        detectionJob?.cancelAndJoin()
        detectionJob = null
        engine?.recorder?.stop()
    }

    suspend fun playTestTone() = playerMutex.withLock {
        val currentEngine = getOrInitializeEngine()
        val generator = createSignalGenerator()
        val frequency = 19000f
        val samples = generator.generateTone(frequency, 2000, config.sampleRate.toFloat()) // 2 seconds
        WLogger.i("Whisper", "Playing $frequency Hz test tone")
        currentEngine.player.play(
            AudioFrame(
                samples = samples,
                sampleRate = config.sampleRate,
                channels = 1,
                timestamp = 0
            )
        )
    }

    suspend fun transmit(data: ByteArray) = playerMutex.withLock {
        val currentEngine = getOrInitializeEngine()
        val packet = WhisperPacket(payload = data)
        val packetEncoder = DefaultPacketEncoder()
        val encodedPacket = packetEncoder.encode(packet, config.fecConfig)
        
        val encoder = FSKEncoder(sampleRate = config.sampleRate.toFloat())
        val samples = encoder.encode(encodedPacket)
        WLogger.i("Whisper", "Transmitting ${data.size} bytes (${samples.size} samples)")
        currentEngine.player.play(
            AudioFrame(
                samples = samples,
                sampleRate = config.sampleRate,
                channels = 1,
                timestamp = 0
            )
        )
    }
    
    fun configure(config: WhisperConfig) {
        this.config = config
    }

    private suspend fun getOrInitializeEngine(): AudioEngine {
        return engine ?: engineMutex.withLock {
            engine ?: createAudioEngine().also {
                it.setup()
                engine = it
            }
        }
    }
}
