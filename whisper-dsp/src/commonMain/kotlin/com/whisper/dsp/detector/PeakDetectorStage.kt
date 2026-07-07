package com.whisper.dsp.detector

import com.whisper.core.model.FrequencyDetection
import com.whisper.core.model.FrequencySpectrum
import kotlin.math.abs

data class PeakDetectorConfig(
    val minimumMagnitude: Float = 0.25f,
    val stabilityToleranceHz: Float = 100f,
    val requiredStableFrames: Int = 5
)

class PeakDetectorStage(
    private val config: PeakDetectorConfig = PeakDetectorConfig()
) {
    private val history = mutableListOf<FrequencyDetection>()
    private var lastEmittedFrequency: Float? = null

    fun detect(spectrum: FrequencySpectrum, timestamp: Long): FrequencyDetection? {
        var maxMagnitude = -1f
        var maxFrequency = 0f

        for (i in spectrum.magnitudes.indices) {
            if (spectrum.magnitudes[i] > maxMagnitude) {
                maxMagnitude = spectrum.magnitudes[i]
                maxFrequency = spectrum.frequencies[i]
            }
        }

        val currentDetection = FrequencyDetection(maxFrequency, maxMagnitude, timestamp)

        // 1. Check Magnitude Threshold
        if (maxMagnitude < config.minimumMagnitude) {
            history.clear()
            return if (lastEmittedFrequency != null) {
                lastEmittedFrequency = null
                FrequencyDetection(0f, 0f, timestamp) // Signal lost
            } else {
                null
            }
        }

        // 2. Check Frequency Stability
        history.add(currentDetection)
        if (history.size > config.requiredStableFrames) {
            history.removeAt(0)
        }

        if (history.size == config.requiredStableFrames) {
            val firstFreq = history.first().frequency
            val isStable = history.all { abs(it.frequency - firstFreq) <= config.stabilityToleranceHz }

            if (isStable) {
                val averageFreq = history.map { it.frequency }.average().toFloat()
                // Only emit if it's the first time or if it changed significantly
                if (lastEmittedFrequency == null || abs(averageFreq - lastEmittedFrequency!!) > config.stabilityToleranceHz) {
                    lastEmittedFrequency = averageFreq
                    return currentDetection.copy(frequency = averageFreq)
                }
            }
        }

        return null
    }
}
