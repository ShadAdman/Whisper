package com.whisper.dsp.detector

import com.whisper.core.model.FrequencyDetection

interface FrequencyDetector {
    fun detectFrequency(samples: FloatArray): FrequencyDetection
}
