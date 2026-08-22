package com.whisper.core.protocol

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MorseEncoderTest {

    private val encoder = MorseEncoder()

    @Test
    fun testEncodeSingleByte() {
        // 0x41 -> "41"
        // 4: ....-
        // 1: .----
        val data = byteArrayOf(0x41)
        val result = encoder.encode(data)
        
        // 4: DOT, ELEMENT, DOT, ELEMENT, DOT, ELEMENT, DOT, ELEMENT, DASH
        // LETTER_GAP
        // 1: DOT, ELEMENT, DASH, ELEMENT, DASH, ELEMENT, DASH, ELEMENT, DASH
        
        val expected = listOf(
            MorseSignal.DOT, MorseSignal.ELEMENT_GAP, 
            MorseSignal.DOT, MorseSignal.ELEMENT_GAP, 
            MorseSignal.DOT, MorseSignal.ELEMENT_GAP, 
            MorseSignal.DOT, MorseSignal.ELEMENT_GAP, 
            MorseSignal.DASH,
            
            MorseSignal.LETTER_GAP,
            
            MorseSignal.DOT, MorseSignal.ELEMENT_GAP, 
            MorseSignal.DASH, MorseSignal.ELEMENT_GAP, 
            MorseSignal.DASH, MorseSignal.ELEMENT_GAP, 
            MorseSignal.DASH, MorseSignal.ELEMENT_GAP, 
            MorseSignal.DASH
        )
        
        assertEquals(expected, result)
    }

    @Test
    fun testEncodeMultipleBytes() {
        // 0x00 0xFF -> "00FF"
        val data = byteArrayOf(0x00, 0xFF.toByte())
        val result = encoder.encode(data)
        
        // 4 digits -> 3 LETTER_GAPs
        val letterGaps = result.count { it == MorseSignal.LETTER_GAP }
        assertEquals(3, letterGaps)
    }
}
