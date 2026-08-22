package com.whisper.core.protocol

class MorseEncoder : ProtocolEncoder<MorseSignal> {

    private val morseMap = mapOf(
        '0' to "-----",
        '1' to ".----",
        '2' to "..---",
        '3' to "...--",
        '4' to "....-",
        '5' to ".....",
        '6' to "-....",
        '7' to "--...",
        '8' to "---..",
        '9' to "----.",
        'A' to ".-",
        'B' to "-...",
        'C' to "-.-.",
        'D' to "-..",
        'E' to ".",
        'F' to "..-."
    )

    override fun encode(data: ByteArray): List<MorseSignal> {
        val hexString = data.toHexString().uppercase()
        val result = mutableListOf<MorseSignal>()

        hexString.forEachIndexed { index, char ->
            val morse = morseMap[char] ?: return@forEachIndexed
            
            morse.forEachIndexed { sIndex, signalChar ->
                result.add(if (signalChar == '.') MorseSignal.DOT else MorseSignal.DASH)
                if (sIndex < morse.length - 1) {
                    result.add(MorseSignal.ELEMENT_GAP)
                }
            }
            
            if (index < hexString.length - 1) {
                // Between hex digits, we add a LETTER_GAP.
                // If we wanted to separate bytes more clearly, we could use WORD_GAP every 2 chars.
                result.add(MorseSignal.LETTER_GAP)
            }
        }

        return result
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
    }
}
