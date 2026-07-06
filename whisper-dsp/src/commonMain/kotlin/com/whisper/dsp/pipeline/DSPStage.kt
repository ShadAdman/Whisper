package com.whisper.dsp.pipeline

import com.whisper.core.model.AudioFrame

interface DSPStage {
    fun process(frame: AudioFrame): AudioFrame
}
