package com.whisper.config

import com.whisper.core.error.FecConfig
import com.whisper.core.protocol.ProtocolType
import com.whisper.crypto.SimpleXorEncryptor
import com.whisper.crypto.WhisperEncryptor

data class WhisperConfig(
    val sampleRate: Int = 48000,
    val carrierFrequency: Float = 19000f,
    val fecConfig: FecConfig = FecConfig(),
    val encryptionKey: ByteArray? = null,
    val encryptor: WhisperEncryptor = SimpleXorEncryptor(),
    val protocolType: ProtocolType = ProtocolType.DEFAULT
)
