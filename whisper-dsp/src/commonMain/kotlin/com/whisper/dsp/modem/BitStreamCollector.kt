package com.whisper.dsp.modem

class BitStreamCollector {
    private val bits = mutableListOf<Int>()

    fun addBit(bit: Int) {
        bits.add(bit)
    }

    fun getBits(): List<Int> = bits.toList()

    fun consume(n: Int) {
        repeat(n) {
            if (bits.isNotEmpty()) bits.removeAt(0)
        }
    }

    fun clear() {
        bits.clear()
    }
}
