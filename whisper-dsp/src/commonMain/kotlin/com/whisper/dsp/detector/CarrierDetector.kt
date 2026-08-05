package com.whisper.dsp.detector

import com.whisper.core.model.CarrierDetected
import com.whisper.core.model.CarrierEvent
import com.whisper.core.model.CarrierLost
import com.whisper.core.model.FrequencyDetection
import com.whisper.core.util.WLogger
import kotlin.math.abs

data class CarrierConfig(
    val frequencyHz: Float = 19000f,
    val toleranceHz: Float = 2000f // Temporarily very wide to see what we detect
)

class CarrierDetector(
    private val config: CarrierConfig = CarrierConfig()
) {
    private var isCarrierPresent = false

    fun process(detection: FrequencyDetection): CarrierEvent? {
        val isInRange = abs(detection.frequency - config.frequencyHz) <= config.toleranceHz
        val hasSignal = detection.magnitude > 0.001f // Use a threshold even with GainStage

        if (isInRange && hasSignal) {
            if (!isCarrierPresent) {
                WLogger.i("CarrierDetector", "Carrier detected at ${detection.frequency}Hz (target: ${config.frequencyHz}Hz)")
                isCarrierPresent = true
                return CarrierDetected(detection)
            }
        } else {
            if (isCarrierPresent) {
                WLogger.i("CarrierDetector", "Carrier lost (last freq: ${detection.frequency}Hz)")
                isCarrierPresent = false
                return CarrierLost
            }
        }
        return null
    }
}
