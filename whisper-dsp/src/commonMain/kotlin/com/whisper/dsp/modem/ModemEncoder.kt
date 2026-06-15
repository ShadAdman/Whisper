package com.whisper.dsp.modem

interface ModemEncoder {
    fun encode(data: ByteArray): FloatArray
}
