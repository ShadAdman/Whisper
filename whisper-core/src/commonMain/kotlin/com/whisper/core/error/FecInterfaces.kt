package com.whisper.core.error

interface FecEncoder {
    fun encode(data: ByteArray): ByteArray
}

interface FecDecoder {
    /**
     * Attempts to decode and correct data.
     * Returns the original data if successful or repairable, null otherwise.
     */
    fun decode(data: ByteArray): ByteArray?
}

data class FecConfig(
    val enabled: Boolean = true,
    val redundancy: Int = 2
)
