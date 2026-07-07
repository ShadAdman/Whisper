package com.whisper.dsp.modem

import com.whisper.core.model.FrequencyDetection

interface ModemDecoder {
    fun decode(detection: FrequencyDetection): Int
}
