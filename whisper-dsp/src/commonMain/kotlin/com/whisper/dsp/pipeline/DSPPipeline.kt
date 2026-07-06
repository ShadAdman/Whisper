package com.whisper.dsp.pipeline

import com.whisper.core.model.AudioFrame

class DSPPipeline(
    private val stages: List<DSPStage>
) {
    fun process(frame: AudioFrame): AudioFrame {
        var currentFrame = frame
        for (stage in stages) {
            currentFrame = stage.process(currentFrame)
        }
        return currentFrame
    }
}
