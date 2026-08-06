package com.whisper.dsp.fft

import com.whisper.core.model.FrequencySpectrum

class DesktopFFTProcessor(private val fftSize: Int) : FFTProcessor {
    override fun process(samples: FloatArray, sampleRate: Float): FrequencySpectrum {
        // Placeholder implementation
        val halfSize = fftSize / 2
        return FrequencySpectrum(
            frequencies = FloatArray(halfSize),
            magnitudes = FloatArray(halfSize)
        )
    }

    override fun release() {
        // No resources to release in placeholder
    }
}

actual fun createFFTProcessor(fftSize: Int): FFTProcessor = DesktopFFTProcessor(fftSize)
