package com.whisper.dsp.modem

interface BitDecoder {
    fun decode(bits: List<Int>): ByteArray
}

class DefaultBitDecoder : BitDecoder {
    override fun decode(bits: List<Int>): ByteArray {
        val bytes = ByteArray(bits.size / 8)
        for (i in bytes.indices) {
            var byte = 0
            for (bitIndex in 0 until 8) {
                val bit = bits[i * 8 + bitIndex]
                if (bit == 1) {
                    byte = byte or (1 shl (7 - bitIndex))
                }
            }
            bytes[i] = byte.toByte()
        }
        return bytes
    }
}
