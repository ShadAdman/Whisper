package com.whisper.dsp.generator

import liquid.*
import kotlinx.cinterop.*
import kotlin.math.PI

class NativeSignalGenerator : SignalGenerator {
    @OptIn(ExperimentalForeignApi::class)
    override fun generateTone(frequency: Float, durationMs: Int): FloatArray {
        val sampleRate = 44100.0f
        val numSamples = (sampleRate * durationMs / 1000.0f).toInt()
        val result = FloatArray(numSamples)
        
        val nco = nco_crcf_create(liquid_ncotype.LIQUID_NCO)
        nco_crcf_set_frequency(nco, (2.0 * PI * frequency / sampleRate).toFloat())
        
        for (i in 0 until numSamples) {
            result[i] = nco_crcf_cos(nco)
            nco_crcf_step(nco)
        }
        
        nco_crcf_destroy(nco)
        
        return result
    }
}

actual fun createSignalGenerator(): SignalGenerator = NativeSignalGenerator()
