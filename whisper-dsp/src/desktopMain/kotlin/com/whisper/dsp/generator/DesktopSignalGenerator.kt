package com.whisper.dsp.generator

import com.whisper.dsp.native.LiquidLibrary
import kotlin.math.PI

class DesktopSignalGenerator : SignalGenerator {
    private val lib = LiquidLibrary.INSTANCE

    override fun generateTone(frequency: Float, durationMs: Int, sampleRate: Float): FloatArray {
        val numSamples = (sampleRate * durationMs / 1000.0f).toInt()
        val result = FloatArray(numSamples)
        
        val nco = lib.nco_crcf_create(LiquidLibrary.LIQUID_NCO)
        lib.nco_crcf_set_frequency(nco, (2.0 * PI * frequency / sampleRate).toFloat())
        
        for (i in 0 until numSamples) {
            result[i] = lib.nco_crcf_cos(nco)
            lib.nco_crcf_step(nco)
        }
        
        lib.nco_crcf_destroy(nco)
        return result
    }
}

actual fun createSignalGenerator(): SignalGenerator = DesktopSignalGenerator()
