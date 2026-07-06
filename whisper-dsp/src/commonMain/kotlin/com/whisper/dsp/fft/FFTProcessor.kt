package com.whisper.dsp.fft

import com.whisper.core.model.FrequencySpectrum

interface FFTProcessor {
    fun process(samples: FloatArray): FrequencySpectrum
    fun release()
}

expect fun createFFTProcessor(fftSize: Int): FFTProcessor
