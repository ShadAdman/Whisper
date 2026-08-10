package com.whisper.dsp.filter

import com.sun.jna.Pointer
import com.whisper.dsp.native.LiquidLibrary

class DesktopBandPassFilter(
    lowCutoff: Float,
    highCutoff: Float,
    sampleRate: Float
) : SignalFilter {
    private val lib = LiquidLibrary.INSTANCE
    
    private val bw = (highCutoff - lowCutoff) / sampleRate
    private val f0 = (highCutoff + lowCutoff) / (2.0f * sampleRate)
    
    private val filter: Pointer = lib.iirfilt_rrrf_create_prototype(
        LiquidLibrary.LIQUID_IIRDES_BUTTER,
        LiquidLibrary.LIQUID_IIRDES_BANDPASS,
        LiquidLibrary.LIQUID_IIRDES_SOS,
        4,    // order
        bw,   // bandwidth
        f0,   // center frequency
        0.1f, // ap
        60.0f // as
    )

    override fun filter(samples: FloatArray): FloatArray {
        val result = FloatArray(samples.size)
        lib.iirfilt_rrrf_execute_block(filter, samples, samples.size, result)
        return result
    }

    override fun release() {
        lib.iirfilt_rrrf_destroy(filter)
    }
}

actual fun createBandPassFilter(lowCutoff: Float, highCutoff: Float, sampleRate: Float): SignalFilter =
    DesktopBandPassFilter(lowCutoff, highCutoff, sampleRate)
