package com.whisper.dsp.pipeline

import com.whisper.core.model.AudioFrame
import com.whisper.dsp.filter.SignalFilter
import com.whisper.dsp.filter.createBandPassFilter

class BandPassStage : DSPStage {
    private var signalFilter: SignalFilter? = null
    private var lastSampleRate: Float = 0f

    companion object {
        private const val LOW_CUTOFF = 17000f
        private const val HIGH_CUTOFF = 21000f
    }

    override fun process(frame: AudioFrame): AudioFrame {
        val sampleRate = frame.sampleRate.toFloat()
        
        if (signalFilter == null || lastSampleRate != sampleRate) {
            signalFilter?.release()
            signalFilter = createBandPassFilter(LOW_CUTOFF, HIGH_CUTOFF, sampleRate)
            lastSampleRate = sampleRate
        }

        val filteredSamples = signalFilter?.filter(frame.samples) ?: frame.samples
        
        var originalMax = 0f
        var filteredMax = 0f
        for (i in frame.samples.indices) {
            if (kotlin.math.abs(frame.samples[i]) > originalMax) originalMax = kotlin.math.abs(frame.samples[i])
            if (kotlin.math.abs(filteredSamples[i]) > filteredMax) filteredMax = kotlin.math.abs(filteredSamples[i])
        }
        if (originalMax > 0.01f) {
            println("BandPassStage: original max $originalMax, filtered max $filteredMax")
        }

        return frame.copy(samples = filteredSamples)
    }

    override fun release() {
        signalFilter?.release()
        signalFilter = null
    }
}
