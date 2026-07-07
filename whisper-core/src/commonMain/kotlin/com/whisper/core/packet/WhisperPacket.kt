package com.whisper.core.packet

data class WhisperPacket(
    val version: Byte = 1,
    val type: Byte = 1,
    val payload: ByteArray,
    val timestamp: Long = 0L
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as WhisperPacket

        if (version != other.version) return false
        if (type != other.type) return false
        if (!payload.contentEquals(other.payload)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = version.toInt()
        result = 31 * result + type.toInt()
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
