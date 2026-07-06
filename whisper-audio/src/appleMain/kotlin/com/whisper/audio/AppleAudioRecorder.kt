package com.whisper.audio

import com.whisper.core.model.AudioFrame
import kotlinx.cinterop.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import platform.AVFoundation.*
import platform.AudioToolbox.*
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class)
class AppleAudioRecorder : AudioRecorder {
    private val _samples = MutableSharedFlow<AudioFrame>()
    override val samples: Flow<AudioFrame> = _samples
    
    private val audioEngine = AVAudioEngine()

    override suspend fun start() {
        val inputNode = audioEngine.inputNode
        val format = inputNode.inputFormatForBus(0u)

        inputNode.installTapOnBus(0u, 1024u, format) { buffer, _ ->
            buffer?.let {
                val frameCount = it.frameLength.toInt()
                val floatData = it.floatChannelData?.get(0) ?: return@let
                
                val samples = FloatArray(frameCount)
                for (i in 0 until frameCount) {
                    samples[i] = floatData[i]
                }
                _samples.tryEmit(
                    AudioFrame(
                        samples = samples,
                        sampleRate = format.sampleRate.toInt(),
                        channels = format.channelCount.toInt(),
                        timestamp = NSDate().timeIntervalSince1970.toLong() * 1000
                    )
                )
            }
        }

        audioEngine.prepare()
        memScoped {
            val errorVar = alloc<ObjCHandleVar>()
            audioEngine.startAndReturnError(errorVar.ptr)
        }
    }

    override suspend fun stop() {
        audioEngine.stop()
        audioEngine.inputNode.removeTapOnBus(0u)
    }
}
