package com.whisper.audio


import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord

import kotlinx.cinterop.*

class AppleAudioEngine : AudioEngine {
    override val recorder: AudioRecorder = AppleAudioRecorder()
    override val player: AudioPlayer = AppleAudioPlayer()

    override suspend fun setup() {
        val session = AVAudioSession.sharedInstance()
        memScoped {
            val errorVar = alloc<ObjCObjectVar<NSError?>>()
            session.setCategory(AVAudioSessionCategoryPlayAndRecord, errorVar.ptr)
            session.setActive(true, errorVar.ptr)
        }
    }

    override suspend fun release() {
        val session = AVAudioSession.sharedInstance()
        memScoped {
            val errorVar = alloc<ObjCObjectVar<NSError?>>()
            session.setActive(false, errorVar.ptr)
        }
    }
}

actual fun createAudioEngine(): AudioEngine = AppleAudioEngine()

actual fun createAudioPlayer(): AudioPlayer = AppleAudioPlayer()

actual fun createAudioRecorder(): AudioRecorder = AppleAudioRecorder()
