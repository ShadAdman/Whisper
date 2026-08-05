package com.whisper.dsp.generator

import kotlin.math.PI
import kotlin.math.sin

class KotlinSignalGenerator : SignalGenerator {
    override fun generateTone(frequency: Float, durationMs: Int, sampleRate: Float): FloatArray {
        val numSamples = (sampleRate * durationMs / 1000f).toInt()
        val result = FloatArray(numSamples)
        
        for (i in 0 until numSamples) {
            val time = i / sampleRate
            result[i] = sin(2.0 * PI * frequency * time).toFloat()
        }
        
        return result
    }
}
