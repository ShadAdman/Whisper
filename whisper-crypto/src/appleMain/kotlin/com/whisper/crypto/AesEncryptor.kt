package com.whisper.crypto

import kotlinx.cinterop.*
import platform.CoreCrypto.*
import platform.Foundation.*
import platform.Security.*
import platform.posix.size_tVar

@OptIn(ExperimentalForeignApi::class)
actual class AesEncryptor : WhisperEncryptor {
    private val kCCAlgorithmAES = 0
    private val kCCBlockSizeAES128 = 16

    actual override fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val iv = ByteArray(kCCBlockSizeAES128)
        val statusRand = iv.usePinned { pinnedIv ->
            SecRandomCopyBytes(kSecRandomDefault, kCCBlockSizeAES128.toULong(), pinnedIv.addressOf(0))
        }
        if (statusRand != 0) throw Exception("Failed to generate random IV")

        return memScoped {
            val dataSize = data.size
            val bufferSize = (dataSize + kCCBlockSizeAES128 + 16).toULong()
            val buffer = allocArray<ByteVar>(bufferSize.toLong())
            val dataOutMoved = alloc<size_tVar>()

            val status = key.usePinned { pinnedKey ->
                iv.usePinned { pinnedIv ->
                    data.usePinned { pinnedData ->
                        CCCrypt(
                            kCCEncrypt,
                            kCCAlgorithmAES.toUInt(),
                            kCCOptionPKCS7Padding.toUInt(),
                            pinnedKey.addressOf(0), key.size.toULong(),
                            pinnedIv.addressOf(0),
                            pinnedData.addressOf(0), data.size.toULong(),
                            buffer, bufferSize,
                            dataOutMoved.ptr
                        )
                    }
                }
            }

            if (status != kCCSuccess) throw Exception("Encryption failed: $status")
            
            iv + buffer.readBytes(dataOutMoved.value.toInt())
        }
    }

    actual override fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        if (data.size < kCCBlockSizeAES128) throw IllegalArgumentException("Invalid data length")
        
        val iv = data.sliceArray(0 until kCCBlockSizeAES128)
        val ciphertext = data.sliceArray(kCCBlockSizeAES128 until data.size)
        
        return memScoped {
            val bufferSize = ciphertext.size.toULong()
            val buffer = allocArray<ByteVar>(bufferSize.toLong())
            val dataOutMoved = alloc<size_tVar>()

            val status = key.usePinned { pinnedKey ->
                iv.usePinned { pinnedIv ->
                    ciphertext.usePinned { pinnedCiphertext ->
                        CCCrypt(
                            kCCDecrypt,
                            kCCAlgorithmAES.toUInt(),
                            kCCOptionPKCS7Padding.toUInt(),
                            pinnedKey.addressOf(0), key.size.toULong(),
                            pinnedIv.addressOf(0),
                            pinnedCiphertext.addressOf(0), ciphertext.size.toULong(),
                            buffer, bufferSize,
                            dataOutMoved.ptr
                        )
                    }
                }
            }

            if (status != kCCSuccess) throw Exception("Decryption failed: $status")
            
            buffer.readBytes(dataOutMoved.value.toInt())
        }
    }
}
