package com.whisper.dsp.detector

import com.whisper.core.model.AudioFrame
import com.whisper.core.model.FrequencyDetection
import com.whisper.dsp.fft.FFTProcessor

class DefaultFrequencyDetector(
    private val fftProcessor: FFTProcessor
) : FrequencyDetector {
    override fun detectFrequency(frame: AudioFrame): FrequencyDetection {
        val spectrum = fftProcessor.process(frame.samples)
        var maxMagnitude = -1f
        var maxFrequency = 0f
        
        for (i in spectrum.magnitudes.indices) {
            if (spectrum.magnitudes[i] > maxMagnitude) {
                maxMagnitude = spectrum.magnitudes[i]
                maxFrequency = spectrum.frequencies[i]
            }
        }
        
        return FrequencyDetection(maxFrequency, maxMagnitude)
    }
}
