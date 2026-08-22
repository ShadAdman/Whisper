package com.whisper.core.protocol

enum class ProtocolType {
    DEFAULT,
    MORSE,
    TAP_CODE
}

enum class MorseSignal {
    DOT,
    DASH,
    ELEMENT_GAP, // Gap between dots and dashes within a character
    LETTER_GAP,  // Gap between characters
    WORD_GAP     // Gap between words
}

interface ProtocolEncoder<T> {
    fun encode(data: ByteArray): List<T>
}
