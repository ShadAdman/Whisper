package com.whisper.core.error

class RepetitionFecEncoder(private val times: Int = 3) : FecEncoder {
    override fun encode(data: ByteArray): ByteArray {
        val result = ByteArray(data.size * times)
        for (i in data.indices) {
            for (t in 0 until times) {
                result[i * times + t] = data[i]
            }
        }
        return result
    }
}

class RepetitionFecDecoder(private val times: Int = 3) : FecDecoder {
    override fun decode(data: ByteArray): ByteArray? {
        if (data.size % times != 0) return null
        
        val resultSize = data.size / times
        val result = ByteArray(resultSize)
        
        for (i in 0 until resultSize) {
            val counts = mutableMapOf<Byte, Int>()
            for (t in 0 until times) {
                val b = data[i * times + t]
                counts[b] = (counts[b] ?: 0) + 1
            }
            
            // Majority vote
            val majority = counts.maxByOrNull { it.value }
            if (majority != null && majority.value >= (times / 2) + 1) {
                result[i] = majority.key
            } else {
                // Cannot reliably determine majority
                return null
            }
        }
        return result
    }
}
