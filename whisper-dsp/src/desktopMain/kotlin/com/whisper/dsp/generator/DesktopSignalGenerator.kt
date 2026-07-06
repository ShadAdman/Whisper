package com.whisper.dsp.generator

class DesktopSignalGenerator : SignalGenerator {
    override fun generateTone(frequency: Float, durationMs: Int): FloatArray {
        // TODO: Implement using JNA or JNI for JVM
        return FloatArray(0)
    }
}

actual fun createSignalGenerator(): SignalGenerator = DesktopSignalGenerator()
