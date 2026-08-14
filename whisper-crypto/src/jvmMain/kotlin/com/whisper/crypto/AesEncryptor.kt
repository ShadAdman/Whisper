package com.whisper.crypto

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

actual class AesEncryptor : WhisperEncryptor {
    private val secureRandom = SecureRandom()
    private val IV_LENGTH = 16

    actual override fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val iv = ByteArray(IV_LENGTH)
        secureRandom.nextBytes(iv)
        
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val ciphertext = cipher.doFinal(data)
        
        return iv + ciphertext
    }

    actual override fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        if (data.size < IV_LENGTH) throw IllegalArgumentException("Invalid data length")
        
        val iv = data.sliceArray(0 until IV_LENGTH)
        val ciphertext = data.sliceArray(IV_LENGTH until data.size)
        
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keySpec = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(ciphertext)
    }
}
