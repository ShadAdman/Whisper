package com.whisper.core.packet

interface CrcCalculator {
    fun calculate(data: ByteArray): ByteArray
}

class Crc16Ccitt : CrcCalculator {
    override fun calculate(data: ByteArray): ByteArray {
        var crc = 0xFFFF // initial value
        val polynomial = 0x1021 // 0001 0000 0010 0001  (CCITT)

        for (byte in data) {
            for (i in 0 until 8) {
                val bit = byte.toInt() shr (7 - i) and 1 == 1
                val c15 = crc shr 15 and 1 == 1
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor polynomial
            }
        }

        crc = crc and 0xFFFF
        return byteArrayOf(
            (crc shr 8).toByte(),
            (crc and 0xFF).toByte()
        )
    }
}
