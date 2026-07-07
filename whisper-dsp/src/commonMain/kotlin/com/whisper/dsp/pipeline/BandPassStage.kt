package com.whisper.dsp.pipeline

import com.whisper.core.model.AudioFrame
import com.whisper.dsp.filter.SignalFilter
import com.whisper.dsp.filter.createBandPassFilter

class BandPassStage : DSPStage {
    private var filter: SignalFilter? = null
    private var lastSampleRate: Float = 0f

    companion object {
        private const val LOW_CUTOFF = 18000f
        private const val HIGH_CUTOFF = 20000f
    }

    override fun process(frame: AudioFrame): AudioFrame {
        val sampleRate = frame.sampleRate.toFloat()
        
        if (filter == null || lastSampleRate != sampleRate) {
            filter?.release()
            filter = createBandPassFilter(LOW_CUTOFF, HIGH_CUTOFF, sampleRate)
            lastSampleRate = sampleRate
        }

        val filteredSamples = filter?.filter(frame.samples) ?: frame.samples
        
        return frame.copy(samples = filteredSamples)
    }

    override fun release() {
        filter?.release()
        filter = null
    }
}
