package com.whisper.dsp.filter

import kotlinx.cinterop.*
import liquid.*

@OptIn(ExperimentalForeignApi::class)
class NativeBandPassFilter(
    lowCutoff: Float,
    highCutoff: Float,
    sampleRate: Float
) : SignalFilter {
    private var filter: CPointer<*>? = null

    init {
        val bw = (highCutoff - lowCutoff) / sampleRate
        val f0 = (highCutoff + lowCutoff) / (2.0f * sampleRate)
        
        filter = iirfilt_rrrf_create_prototype(
            LIQUID_IIRDES_BUTTER,
            LIQUID_IIRDES_BANDPASS,
            LIQUID_IIRDES_SOS,
            4u,
            bw,
            f0,
            0.1f,
            60.0f
        )
    }

    override fun filter(samples: FloatArray): FloatArray {
        val n = samples.size
        val result = FloatArray(n)
        
        val f = filter ?: return samples

        samples.usePinned { samplesPinned ->
            result.usePinned { resultPinned ->
                iirfilt_rrrf_execute_block(
                    f as iirfilt_rrrf,
                    samplesPinned.addressOf(0),
                    n.toUInt(),
                    resultPinned.addressOf(0)
                )
            }
        }
        
        return result
    }

    override fun release() {
        filter?.let {
            iirfilt_rrrf_destroy(it as iirfilt_rrrf)
        }
        filter = null
    }
}

actual fun createBandPassFilter(lowCutoff: Float, highCutoff: Float, sampleRate: Float): SignalFilter =
    NativeBandPassFilter(lowCutoff, highCutoff, sampleRate)
