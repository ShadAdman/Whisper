package com.whisper.core.packet

data class WhisperPacket(
    val payload: ByteArray,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WhisperPacket

        if (!payload.contentEquals(other.payload)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = payload.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
