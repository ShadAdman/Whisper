package com.whisper.audio

import android.content.Context
import androidx.startup.Initializer

class AudioInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        ContextHolder.context = context
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
