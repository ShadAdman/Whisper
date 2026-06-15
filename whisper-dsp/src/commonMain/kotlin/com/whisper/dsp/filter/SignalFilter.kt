package com.whisper.dsp.filter

interface SignalFilter {
    fun filter(samples: FloatArray): FloatArray
}
