package com.whisper.audio

import kotlinx.cinterop.*
import platform.AVFoundation.*
import platform.AudioToolbox.*
import platform.Foundation.*

class IOSAudioRecorder : AudioRecorder {
    private var listener: ((FloatArray) -> Unit)? = null
    private val audioEngine = AVAudioEngine()

    override fun setListener(listener: (FloatArray) -> Unit) {
        this.listener = listener
    }

    override suspend fun start() {
        val inputNode = audioEngine.inputNode
        val format = inputNode.inputFormatForBus(0)

        inputNode.installTapOnBus(0, 1024u, format) { buffer, _ ->
            buffer?.let {
                val frameCount = it.frameLength.toInt()
                val channels = it.format.channelCount.toInt()
                val floatData = it.floatChannelData?.get(0) ?: return@let
                
                val samples = FloatArray(frameCount)
                for (i in 0 until frameCount) {
                    samples[i] = floatData[i]
                }
                listener?.invoke(samples)
            }
        }

        audioEngine.prepare()
        val error = memScoped {
            alloc<ObjCHandleVar>().let { errorVar ->
                audioEngine.startAndReturnError(errorVar.ptr)
                errorVar.value
            }
        }
        
        if (error != null) {
            // Handle error
        }
    }

    override suspend fun stop() {
        audioEngine.stop()
        audioEngine.inputNode.removeTapOnBus(0)
    }
}
