package com.whisper.dsp.pipeline

import com.whisper.core.model.AudioFrame
import kotlin.math.PI
import kotlin.math.cos

class WindowStage : DSPStage {
    private var cachedCoefficients: FloatArray? = null

    override fun process(frame: AudioFrame): AudioFrame {
        val n = frame.samples.size
        if (n <= 1) return frame

        val coefficients = getCoefficients(n)
        val windowedSamples = FloatArray(n)
        
        for (i in 0 until n) {
            windowedSamples[i] = frame.samples[i] * coefficients[i]
        }
        
        return frame.copy(samples = windowedSamples)
    }

    private fun getCoefficients(n: Int): FloatArray {
        val current = cachedCoefficients
        if (current != null && current.size == n) {
            return current
        }
        
        val newCoefficients = FloatArray(n) { i ->
            (0.5f * (1f - cos(2.0 * PI * i / (n - 1)))).toFloat()
        }
        cachedCoefficients = newCoefficients
        return newCoefficients
    }
}
