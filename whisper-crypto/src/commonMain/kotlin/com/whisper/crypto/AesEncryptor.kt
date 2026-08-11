package com.whisper.crypto

/**
 * AES implementation of [WhisperEncryptor].
 * Uses platform-specific crypto APIs.
 */
expect class AesEncryptor() : WhisperEncryptor {
    override fun encrypt(data: ByteArray, key: ByteArray): ByteArray
    override fun decrypt(data: ByteArray, key: ByteArray): ByteArray
}
