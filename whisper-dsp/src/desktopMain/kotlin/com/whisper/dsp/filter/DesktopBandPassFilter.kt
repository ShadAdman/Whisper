package com.whisper.dsp.filter

import kotlin.math.*

class DesktopBandPassFilter(
    lowCutoff: Float,
    highCutoff: Float,
    sampleRate: Float
) : SignalFilter {
    
    private val f0 = (lowCutoff + highCutoff) / 2.0
    private val bw = (highCutoff - lowCutoff).toDouble()
    private val q = f0 / bw
    private val omega = 2.0 * PI * f0 / sampleRate
    private val sinW = sin(omega)
    private val cosW = cos(omega)
    private val alpha = sinW / (2.0 * q)

    private val b0 = sinW / 2.0
    private val b1 = 0.0
    private val b2 = -sinW / 2.0
    private val a0 = 1.0 + alpha
    private val a1 = -2.0 * cosW
    private val a2 = 1.0 - alpha

    private val nb0 = (b0 / a0).toFloat()
    private val nb1 = (b1 / a0).toFloat()
    private val nb2 = (b2 / a0).toFloat()
    private val na1 = (a1 / a0).toFloat()
    private val na2 = (a2 / a0).toFloat()

    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f

    override fun filter(samples: FloatArray): FloatArray {
        val result = FloatArray(samples.size)
        for (i in samples.indices) {
            val x0 = samples[i]
            val y0 = nb0 * x0 + nb1 * x1 + nb2 * x2 - na1 * y1 - na2 * y2
            
            x2 = x1
            x1 = x0
            y2 = y1
            y1 = y0
            
            result[i] = y0
        }
        return result
    }

    override fun release() {
        // No-op
    }
}

actual fun createBandPassFilter(lowCutoff: Float, highCutoff: Float, sampleRate: Float): SignalFilter =
    DesktopBandPassFilter(lowCutoff, highCutoff, sampleRate)
