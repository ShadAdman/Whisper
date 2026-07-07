package com.whisper.dsp.pipeline

import com.whisper.core.model.AudioFrame
import kotlin.math.abs

/**
 * GainStage implements simple peak normalization for individual [AudioFrame]s.
 * It scales the samples so that the peak absolute value reaches [targetPeak].
 * If the frame is nearly silent (peak < [silenceThreshold]), no gain is applied.
 */
class GainStage(
    private val targetPeak: Float = 1.0f,
    private val silenceThreshold: Float = 1e-6f
) : DSPStage {

    override fun process(frame: AudioFrame): AudioFrame {
        val samples = frame.samples
        if (samples.isEmpty()) return frame

        var maxAbs = 0f
        for (sample in samples) {
            val a = abs(sample)
            if (a > maxAbs) {
                maxAbs = a
            }
        }

        if (maxAbs < silenceThreshold || abs(maxAbs - targetPeak) < 1e-4f) {
            return frame
        }

        val gain = targetPeak / maxAbs
        val normalizedSamples = FloatArray(samples.size)
        for (i in samples.indices) {
            normalizedSamples[i] = samples[i] * gain
        }

        return frame.copy(samples = normalizedSamples)
    }
}
