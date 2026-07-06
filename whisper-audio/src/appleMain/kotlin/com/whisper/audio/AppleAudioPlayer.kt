package com.whisper.audio

import com.whisper.core.model.AudioFrame
import kotlinx.cinterop.*
import platform.AVFoundation.*
import platform.Foundation.*

class AppleAudioPlayer : AudioPlayer {
    private val audioEngine = AVAudioEngine()
    private val playerNode = AVAudioPlayerNode()

    override suspend fun play(frame: AudioFrame) {
        stop()
        
        audioEngine.attachNode(playerNode)
        
        val format = AVAudioFormat(
            standardFormatWithSampleRate = frame.sampleRate.toDouble(),
            channels = frame.channels.toUInt()
        )
        
        audioEngine.connect(playerNode, audioEngine.mainMixerNode, format)
        
        audioEngine.prepare()
        memScoped {
            val errorVar = alloc<ObjCHandleVar>()
            audioEngine.startAndReturnError(errorVar.ptr)
        }

        val pcmBuffer = AVAudioPCMBuffer(
            format = format,
            frameCapacity = frame.samples.size.toUInt()
        ) ?: return
        
        pcmBuffer.frameLength = frame.samples.size.toUInt()
        val data = pcmBuffer.floatChannelData?.get(0) ?: return
        for (i in frame.samples.indices) {
            data[i] = frame.samples[i]
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
