package com.whisper.crypto

actual class AesEncryptor : WhisperEncryptor {
    actual override fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        throw NotImplementedError("AES not implemented for Linux yet")
    }

    actual override fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        throw NotImplementedError("AES not implemented for Linux yet")
    }
}
