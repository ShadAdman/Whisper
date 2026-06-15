package com.whisper.audio

import kotlinx.cinterop.*
import platform.AVFoundation.*
import platform.Foundation.*

class IOSAudioPlayer : AudioPlayer {
    private val audioEngine = AVAudioEngine()
    private val playerNode = AVAudioPlayerNode()

    override suspend fun play(samples: FloatArray) {
        stop()
        
        audioEngine.attachNode(playerNode)
        
        val format = AVAudioFormat(
            standardFormatWithSampleRate = 48000.0,
            channels = 1u
        )
        
        audioEngine.connect(playerNode, audioEngine.mainMixerNode, format)
        
        audioEngine.prepare()
        memScoped {
            val errorVar = alloc<ObjCHandleVar>()
            audioEngine.startAndReturnError(errorVar.ptr)
        }

        val pcmBuffer = AVAudioPCMBuffer(
            format = format,
            frameCapacity = samples.size.toUInt()
        ) ?: return
        
        pcmBuffer.frameLength = samples.size.toUInt()
        val data = pcmBuffer.floatChannelData?.get(0) ?: return
        for (i in samples.indices) {
            data[i] = samples[i]
        }

        playerNode.scheduleBuffer(pcmBuffer, atTime = null, options = 0u, completionHandler = null)
        playerNode.play()
    }

    override suspend fun stop() {
        if (playerNode.playing) {
            playerNode.stop()
        }
        if (audioEngine.running) {
            audioEngine.stop()
        }
    }
}
