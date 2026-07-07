package com.whisper.dsp.detector

import com.whisper.core.model.CarrierDetected
import com.whisper.core.model.CarrierEvent
import com.whisper.core.model.CarrierLost
import com.whisper.core.model.FrequencyDetection
import kotlin.math.abs

data class CarrierConfig(
    val frequencyHz: Float = 19000f,
    val toleranceHz: Float = 100f
)

class CarrierDetector(
    private val config: CarrierConfig = CarrierConfig()
) {
    private var isCarrierPresent = false

    fun process(detection: FrequencyDetection): CarrierEvent? {
        val isInRange = abs(detection.frequency - config.frequencyHz) <= config.toleranceHz
        val hasSignal = detection.magnitude > 0f // PeakDetectorStage already filtered by minimumMagnitude

        return if (isInRange && hasSignal) {
            if (!isCarrierPresent) {
                isCarrierPresent = true
                CarrierDetected(detection)
            } else {
                null
            }
        } else {
            if (isCarrierPresent) {
                isCarrierPresent = false
                CarrierLost
            } else {
                null
            }
        }
    }
}
