package com.whisper.dsp.fft

import com.whisper.core.model.FrequencySpectrum

class AndroidFFTProcessor(private val fftSize: Int) : FFTProcessor {
    private var nativePtr: Long = 0

    init {
        nativePtr = nativeCreate(fftSize)
    }

    override fun process(samples: FloatArray): FrequencySpectrum {
        val magnitudes = FloatArray(fftSize / 2 + 1)
        nativeProcess(nativePtr, samples, magnitudes)
        
        val frequencies = FloatArray(magnitudes.size) { i ->
            i * (48000f / fftSize)
        }
        
        return FrequencySpectrum(frequencies, magnitudes)
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

    private external fun nativeCreate(fftSize: Int): Long
    private external fun nativeProcess(ptr: Long, samples: FloatArray, magnitudes: FloatArray)
    private external fun nativeDestroy(ptr: Long)

    companion object {
        init {
            System.loadLibrary("whisper_dsp_jni")
        }
    }
}

actual fun createFFTProcessor(fftSize: Int): FFTProcessor = AndroidFFTProcessor(fftSize)
