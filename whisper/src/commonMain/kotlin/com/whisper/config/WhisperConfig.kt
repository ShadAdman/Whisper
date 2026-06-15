package com.whisper.config

data class WhisperConfig(
    val sampleRate: Int = 48000,
    val carrierFrequency: Float = 19000f
)
