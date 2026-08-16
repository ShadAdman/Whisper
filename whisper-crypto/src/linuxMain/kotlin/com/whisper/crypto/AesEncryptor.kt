package com.whisper.crypto

import kotlinx.cinterop.*
import openssl.*

@OptIn(ExperimentalForeignApi::class)
actual class AesEncryptor : WhisperEncryptor {
    private val AES_BLOCK_SIZE = 16

    actual override fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val iv = ByteArray(AES_BLOCK_SIZE)
        iv.usePinned { pinnedIv ->
            if (RAND_bytes(pinnedIv.addressOf(0).reinterpret(), AES_BLOCK_SIZE) != 1) {
                throw Exception("Failed to generate random IV")
            }
        }

        return memScoped {
            val ctx = EVP_CIPHER_CTX_new() ?: throw Exception("Failed to create EVP_CIPHER_CTX")
            try {
                val cipher = when (key.size) {
                    16 -> EVP_aes_128_cbc()
                    24 -> EVP_aes_192_cbc()
                    32 -> EVP_aes_256_cbc()
                    else -> throw IllegalArgumentException("Invalid key size: ${key.size}")
                }

                val outBuf = allocArray<ByteVar>(data.size + AES_BLOCK_SIZE)
                val outLen = alloc<IntVar>()
                val finalLen = alloc<IntVar>()

                key.usePinned { pinnedKey ->
                    iv.usePinned { pinnedIv ->
                        if (EVP_EncryptInit_ex(ctx, cipher, null, pinnedKey.addressOf(0).reinterpret(), pinnedIv.addressOf(0).reinterpret()) != 1) {
                            throw Exception("EVP_EncryptInit_ex failed")
                        }
                    }
                }

                data.usePinned { pinnedData ->
                    if (EVP_EncryptUpdate(ctx, outBuf.reinterpret(), outLen.ptr, pinnedData.addressOf(0).reinterpret(), data.size) != 1) {
                        throw Exception("EVP_EncryptUpdate failed")
                    }
                }

                if (EVP_EncryptFinal_ex(ctx, (outBuf + outLen.value)!!.reinterpret(), finalLen.ptr) != 1) {
                    throw Exception("EVP_EncryptFinal_ex failed")
                }

                iv + outBuf.readBytes(outLen.value + finalLen.value)
            } finally {
                EVP_CIPHER_CTX_free(ctx)
            }
        }
    }

    actual override fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        if (data.size < AES_BLOCK_SIZE) throw IllegalArgumentException("Invalid data length")
        
        val iv = data.sliceArray(0 until AES_BLOCK_SIZE)
        val ciphertext = data.sliceArray(AES_BLOCK_SIZE until data.size)

        return memScoped {
            val ctx = EVP_CIPHER_CTX_new() ?: throw Exception("Failed to create EVP_CIPHER_CTX")
            try {
                val cipher = when (key.size) {
                    16 -> EVP_aes_128_cbc()
                    24 -> EVP_aes_192_cbc()
                    32 -> EVP_aes_256_cbc()
                    else -> throw IllegalArgumentException("Invalid key size: ${key.size}")
                }

                val outBuf = allocArray<ByteVar>(ciphertext.size + AES_BLOCK_SIZE)
                val outLen = alloc<IntVar>()
                val finalLen = alloc<IntVar>()

                key.usePinned { pinnedKey ->
                    iv.usePinned { pinnedIv ->
                        if (EVP_DecryptInit_ex(ctx, cipher, null, pinnedKey.addressOf(0).reinterpret(), pinnedIv.addressOf(0).reinterpret()) != 1) {
                            throw Exception("EVP_DecryptInit_ex failed")
                        }
                    }
                }

                ciphertext.usePinned { pinnedCiphertext ->
                    if (EVP_DecryptUpdate(ctx, outBuf.reinterpret(), outLen.ptr, pinnedCiphertext.addressOf(0).reinterpret(), ciphertext.size) != 1) {
                        throw Exception("EVP_DecryptUpdate failed")
                    }
                }

                if (EVP_DecryptFinal_ex(ctx, (outBuf + outLen.value)!!.reinterpret(), finalLen.ptr) != 1) {
                    throw Exception("EVP_DecryptFinal_ex failed (possibly wrong key or corrupted data)")
                }

                outBuf.readBytes(outLen.value + finalLen.value)
            } finally {
                EVP_CIPHER_CTX_free(ctx)
            }
        }
    }
}
