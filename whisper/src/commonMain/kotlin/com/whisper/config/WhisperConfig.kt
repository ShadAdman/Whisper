package com.whisper.config

import com.whisper.core.error.FecConfig

data class WhisperConfig(
    val sampleRate: Int = 48000,
    val carrierFrequency: Float = 19000f,
    val fecConfig: FecConfig = FecConfig()
)
