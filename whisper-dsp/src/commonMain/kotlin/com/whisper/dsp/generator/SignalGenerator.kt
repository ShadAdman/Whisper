package com.whisper.dsp.generator

interface SignalGenerator {
    fun generateTone(
        frequency: Float,
        durationMs: Int
    ): FloatArray

    companion object {
        fun create(): SignalGenerator = KotlinSignalGenerator()
    }
}

expect fun createSignalGenerator(): SignalGenerator
