package com.whisper.dsp.detector

import com.whisper.core.model.AudioFrame
import com.whisper.core.model.FrequencyDetection

interface FrequencyDetector {
    fun detectFrequency(frame: AudioFrame): FrequencyDetection
}
