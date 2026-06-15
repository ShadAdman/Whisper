package com.whisper.dsp.detector

interface FrequencyDetector {
    fun detectFrequency(samples: FloatArray): Float?
}
