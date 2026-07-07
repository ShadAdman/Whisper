package com.whisper.dsp.filter

interface SignalFilter {
    fun filter(samples: FloatArray): FloatArray
    fun release()
}

expect fun createBandPassFilter(lowCutoff: Float, highCutoff: Float, sampleRate: Float): SignalFilter
