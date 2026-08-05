package com.whisper.doc

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(
        title = "Whisper Doc",
        canvasElementId = "compose-target"
    ) {
        LandingPage()
    }
}
