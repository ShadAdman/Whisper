package com.whisper.dsp.generator

class AndroidSignalGenerator : SignalGenerator {
    companion object {
        init {
            System.loadLibrary("liquid")
        }
    }

    override fun generateTone(frequency: Float, durationMs: Int): FloatArray {
        // TODO: Implement JNI call to liquid-dsp
        return FloatArray(0)
    }
}

actual fun createSignalGenerator(): SignalGenerator = AndroidSignalGenerator()
