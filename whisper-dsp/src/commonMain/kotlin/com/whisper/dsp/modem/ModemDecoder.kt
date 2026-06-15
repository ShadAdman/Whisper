package com.whisper.dsp.modem

interface ModemDecoder {
    fun decode(samples: FloatArray): ByteArray?
}
