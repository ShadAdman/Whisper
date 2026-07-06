package com.whisper.core.model

data class FrequencySpectrum(
    val frequencies: FloatArray,
    val magnitudes: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FrequencySpectrum

        if (!frequencies.contentEquals(other.frequencies)) return false
        if (!magnitudes.contentEquals(other.magnitudes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = frequencies.contentHashCode()
        result = 31 * result + magnitudes.contentHashCode()
        return result
    }
}
