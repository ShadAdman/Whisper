package com.whisper.crypto

/**
 * Interface for encrypting and decrypting data transmitted via Whisper.
 */
interface WhisperEncryptor {
    fun encrypt(data: ByteArray, key: ByteArray): ByteArray
    fun decrypt(data: ByteArray, key: ByteArray): ByteArray
}

/**
 * A simple XOR implementation of [WhisperEncryptor] for demonstration purposes.
 * WARNING: This is not secure for production use.
 */
class SimpleXorEncryptor : WhisperEncryptor {
    override fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        if (key.isEmpty()) return data
        val result = ByteArray(data.size)
        for (i in data.indices) {
            result[i] = (data[i].toInt() xor key[i % key.size].toInt()).toByte()
        }
        return result
    }

    override fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        return encrypt(data, key) // XOR is its own inverse
    }
}
