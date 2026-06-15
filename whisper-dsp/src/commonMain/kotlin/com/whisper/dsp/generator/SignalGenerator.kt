package com.whisper.dsp.generator

interface SignalGenerator {
    fun generateTone(
        frequency: Float,
        durationMs: Int
    ): FloatArray
}
