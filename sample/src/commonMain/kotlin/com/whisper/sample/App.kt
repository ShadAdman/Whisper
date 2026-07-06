package com.whisper.sample

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whisper.api.Whisper
import com.whisper.core.model.FrequencyDetection
import kotlinx.coroutines.launch

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    var detectedFreq by remember { mutableStateOf(0f) }
    var magnitude by remember { mutableStateOf(0f) }
    var isListening by remember { mutableStateOf(value = false) }

    LaunchedEffect(isListening) {
        if (isListening) {
            Whisper.detectedFrequency.collect { detection: FrequencyDetection ->
                detectedFreq = detection.frequency
                magnitude = detection.magnitude
            }
        }
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Whisper Tone Detection", style = MaterialTheme.typography.h4)

            Button(
                onClick = {
                    scope.launch {
                        Whisper.playTestTone()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Play 19 kHz")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    scope.launch {
                        isListening = true
                        Whisper.startListening()
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text("Start Listening")
                }

                Button(onClick = {
                    scope.launch {
                        isListening = false
                        Whisper.stopListening()
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text("Stop Listening")
                }
            }

            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detected:", style = MaterialTheme.typography.h6)
                    Text("${detectedFreq.toInt()} Hz", style = MaterialTheme.typography.h3)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Magnitude:", style = MaterialTheme.typography.h6)
                    Text(magnitude.toString(), style = MaterialTheme.typography.h4)
                }
            }
        }
    }
}
