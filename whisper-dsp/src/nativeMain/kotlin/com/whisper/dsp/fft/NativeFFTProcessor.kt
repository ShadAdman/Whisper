package com.whisper.dsp.fft

import com.whisper.core.model.FrequencySpectrum
import liquid.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
class NativeFFTProcessor(private val fftSize: Int) : FFTProcessor {
    private val input = nativeHeap.allocArray<FloatVar>(fftSize * 2)
    private val output = nativeHeap.allocArray<FloatVar>(fftSize * 2)
    private val plan = fft_create_plan(fftSize.toUInt(), input.reinterpret(), output.reinterpret(), -1, 0)!!

    override fun process(samples: FloatArray): FrequencySpectrum {
        for (i in 0 until fftSize) {
            if (i < samples.size) {
                input[2 * i] = samples[i]
                input[2 * i + 1] = 0f
            } else {
                input[2 * i] = 0f
                input[2 * i + 1] = 0f
            }
        }

        fft_execute(plan)

        val magnitudesSize = fftSize / 2 + 1
        val magnitudes = FloatArray(magnitudesSize)
        for (i in 0 until magnitudesSize) {
            val re = output[2 * i]
            val im = output[2 * i + 1]
            magnitudes[i] = kotlin.math.sqrt(re * re + im * im)
        }

        val frequencies = FloatArray(magnitudesSize) { i ->
            i * (48000f / fftSize)
        }

        return FrequencySpectrum(frequencies, magnitudes)
    }

    override fun release() {
        fft_destroy_plan(plan)
        nativeHeap.free(input)
        nativeHeap.free(output)
    }
}

actual fun createFFTProcessor(fftSize: Int): FFTProcessor = NativeFFTProcessor(fftSize)
