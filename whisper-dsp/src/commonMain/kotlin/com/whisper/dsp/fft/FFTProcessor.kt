package com.whisper.dsp.fft

interface FFTProcessor {
    fun process(samples: FloatArray): FloatArray
}
