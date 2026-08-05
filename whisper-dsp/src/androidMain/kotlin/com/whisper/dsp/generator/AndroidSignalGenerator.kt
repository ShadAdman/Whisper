package com.whisper.dsp.generator

class AndroidSignalGenerator : SignalGenerator {
    companion object {
        init {
            try {
                System.loadLibrary("liquid")
            } catch (e: UnsatisfiedLinkError) {
                // Ignore if already loaded or not found in system path (might be bundled in APK)
            }
            System.loadLibrary("whisper_dsp_jni")
        }
    }

    override fun generateTone(frequency: Float, durationMs: Int, sampleRate: Float): FloatArray {
        return nativeGenerateTone(frequency, durationMs, sampleRate)
    }

    private external fun nativeGenerateTone(frequency: Float, durationMs: Int, sampleRate: Float): FloatArray
}

actual fun createSignalGenerator(): SignalGenerator = AndroidSignalGenerator()
