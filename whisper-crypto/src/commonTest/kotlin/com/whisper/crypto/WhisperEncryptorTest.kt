package com.whisper.crypto

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContentEquals

class WhisperEncryptorTest {
    @Test
    fun testSimpleXorEncryptor() {
        val encryptor = SimpleXorEncryptor()
        val data = "Hello Whisper".encodeToByteArray()
        val key = "secret".encodeToByteArray()
        
        val encrypted = encryptor.encrypt(data, key)
        assertTrue(encrypted.size == data.size)
        
        val decrypted = encryptor.decrypt(encrypted, key)
        assertContentEquals(data, decrypted)
    }
    
    @Test
    fun testNoKey() {
        val encryptor = SimpleXorEncryptor()
        val data = "Hello Whisper".encodeToByteArray()
        val key = byteArrayOf()
        
        val encrypted = encryptor.encrypt(data, key)
        assertContentEquals(data, encrypted)
    }
}
