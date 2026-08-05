package com.whisper.doc

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

sealed class Screen {
    object Landing : Screen()
    object Documentation : Screen()
}

@Composable
fun NavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Landing) }

    WhisperTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                is Screen.Landing -> LandingPage(onGetStarted = { currentScreen = Screen.Documentation })
                is Screen.Documentation -> DocumentationPage(onBack = { currentScreen = Screen.Landing })
            }
        }
    }
}
