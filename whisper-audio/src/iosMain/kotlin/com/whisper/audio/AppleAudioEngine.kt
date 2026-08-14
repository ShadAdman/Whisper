package com.whisper.audio


import platform.AVFoundation.*
import platform.AVFAudio.*
import platform.Foundation.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
class AppleAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = AppleAudioRecorder()
    override val player: AudioPlayer = AppleAudioPlayer()

    override suspend fun setup() {
        val session = AVAudioSession.sharedInstance()
        memScoped {
            val errorVar = alloc<ObjCObjectVar<platform.Foundation.NSError?>>()
            session.setCategory(AVAudioSessionCategoryPlayAndRecord, errorVar.ptr)
            session.setActive(true, errorVar.ptr)
        }
    }

    override suspend fun release() {
        val session = AVAudioSession.sharedInstance()
        memScoped {
            val errorVar = alloc<ObjCObjectVar<platform.Foundation.NSError?>>()
            session.setActive(false, errorVar.ptr)
        }
    }
}

actual fun createAudioEngine(): AudioEngine = AppleAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = AppleAudioPlayer()

actual fun createAudioRecorder(): AudioRecorder = AppleAudioRecorder()
