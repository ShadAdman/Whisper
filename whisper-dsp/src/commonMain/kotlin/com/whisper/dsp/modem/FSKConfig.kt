package com.whisper.dsp.modem

data class FSKConfig(
    val frequencyZero: Float = 18800f,
    val frequencyOne: Float = 19200f,
    val frequencyTolerance: Float = 100f
)
