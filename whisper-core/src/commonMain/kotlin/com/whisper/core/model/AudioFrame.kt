package com.whisper.core.model

data class AudioFrame(
    val samples: FloatArray,
    val sampleRate: Int,
    val channels: Int,
    val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AudioFrame

        if (!samples.contentEquals(other.samples)) return false
        if (sampleRate != other.sampleRate) return false
        if (channels != other.channels) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = samples.contentHashCode()
        result = 31 * result + sampleRate
        result = 31 * result + channels
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
