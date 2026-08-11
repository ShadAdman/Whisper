package com.whisper.dsp.fft

import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.whisper.core.model.FrequencySpectrum
import com.whisper.dsp.native.LiquidLibrary
import kotlin.math.sqrt

class DesktopFFTProcessor(private val fftSize: Int) : FFTProcessor {
    private val lib = LiquidLibrary.INSTANCE
    
    // Each liquid_float_complex is 2 floats (8 bytes)
    private val inputBuffer = Memory((fftSize * 8).toLong())
    private val outputBuffer = Memory((fftSize * 8).toLong())
    private val plan: Pointer = lib.fft_create_plan(
        fftSize, 
        inputBuffer, 
        outputBuffer, 
        LiquidLibrary.LIQUID_FFT_FORWARD, 
        0
    )

    override fun process(samples: FloatArray, sampleRate: Float): FrequencySpectrum {
        // Prepare input: real part = samples, imag part = 0
        for (i in 0 until fftSize) {
            val value = if (i < samples.size) samples[i] else 0f
            inputBuffer.setFloat((i * 8).toLong(), value)
            inputBuffer.setFloat((i * 8 + 4).toLong(), 0f)
        }

        lib.fft_execute(plan)

        val halfSize = fftSize / 2
        val frequencies = FloatArray(halfSize)
        val magnitudes = FloatArray(halfSize)

        for (i in 0 until halfSize) {
            val re = outputBuffer.getFloat((i * 8).toLong())
            val im = outputBuffer.getFloat((i * 8 + 4).toLong())
            magnitudes[i] = sqrt(re * re + im * im)
            frequencies[i] = (i * sampleRate) / fftSize
        }

        return FrequencySpectrum(frequencies, magnitudes)
    }

    override fun release() {
        lib.fft_destroy_plan(plan)
    }
}

actual fun createFFTProcessor(fftSize: Int): FFTProcessor = DesktopFFTProcessor(fftSize)
