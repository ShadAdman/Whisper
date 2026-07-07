package com.whisper.dsp.modem

import com.whisper.core.model.FrequencyDetection
import kotlin.math.abs

class FSKDecoder(
    private val config: FSKConfig = FSKConfig()
) : ModemDecoder {

    override fun decode(detection: FrequencyDetection): Int {
        if (detection.magnitude <= 0f || detection.frequency <= 0f) return -1 // -1 for lost signal

        val isZero = abs(detection.frequency - config.frequencyZero) <= config.frequencyTolerance
        if (isZero) return 0

        val isOne = abs(detection.frequency - config.frequencyOne) <= config.frequencyTolerance
        if (isOne) return 1

        return -1
    }
}
