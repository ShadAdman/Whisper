package com.whisper.dsp.filter

class AndroidBandPassFilter(
    lowCutoff: Float,
    highCutoff: Float,
    sampleRate: Float
) : SignalFilter {
    private var nativePtr: Long = 0

    init {
        nativePtr = nativeCreate(lowCutoff, highCutoff, sampleRate)
    }

    override fun filter(samples: FloatArray): FloatArray {
        val filteredSamples = FloatArray(samples.size)
        nativeProcess(nativePtr, samples, filteredSamples)
        return filteredSamples
    }

    override fun release() {
        if (nativePtr != 0L) {
            nativeDestroy(nativePtr)
            nativePtr = 0
        }
    }

    protected fun finalize() {
        release()
    }

    private external fun nativeCreate(lowCutoff: Float, highCutoff: Float, sampleRate: Float): Long
    private external fun nativeProcess(ptr: Long, samples: FloatArray, filteredSamples: FloatArray)
    private external fun nativeDestroy(ptr: Long)

    companion object {
        init {
            System.loadLibrary("whisper_dsp_jni")
        }
    }
}

actual fun createBandPassFilter(lowCutoff: Float, highCutoff: Float, sampleRate: Float): SignalFilter =
    AndroidBandPassFilter(lowCutoff, highCutoff, sampleRate)
