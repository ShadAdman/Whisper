package com.whisper.dsp.modem

import kotlin.math.PI
import kotlin.math.sin

class FSKEncoder(
    private val config: FSKConfig = FSKConfig(),
    private val sampleRate: Float = 48000f,
    private val symbolDurationMs: Int = 200 // Increased for reliability
) : ModemEncoder {

    override fun encode(data: ByteArray): FloatArray {
        val bits = mutableListOf<Int>()
        for (byte in data) {
            val b = byte.toInt()
            for (i in 7 downTo 0) {
                bits.add((b shr i) and 1)
            }
        }

        val numSamplesPerSymbol = (sampleRate * symbolDurationMs / 1000f).toInt()
        val totalSamples = bits.size * numSamplesPerSymbol
        val result = FloatArray(totalSamples)

        for (bitIndex in bits.indices) {
            val bit = bits[bitIndex]
            val freq = if (bit == 0) config.frequencyZero else config.frequencyOne
            val offset = bitIndex * numSamplesPerSymbol
            
            for (i in 0 until numSamplesPerSymbol) {
                val time = i / sampleRate
                result[offset + i] = sin(2.0 * PI * freq * time).toFloat()
            }
        }

        return result
    }
}
