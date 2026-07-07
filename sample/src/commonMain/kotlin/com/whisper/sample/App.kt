package com.whisper.sample

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whisper.api.Whisper
import com.whisper.core.model.CarrierDetected
import com.whisper.core.model.CarrierLost
import com.whisper.core.model.FrequencyDetection
import kotlinx.coroutines.launch

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    var detectedFreq by remember { mutableStateOf(0f) }
    var magnitude by remember { mutableStateOf(0f) }
    var isListening by remember { mutableStateOf(value = false) }
    var isCarrierDetected by remember { mutableStateOf(value = false) }
    var receivedText by remember { mutableStateOf("") }
    var decodedBits by remember { mutableStateOf("") }
    var textToTransmit by remember { mutableStateOf("HELLO") }

    LaunchedEffect(isListening) {
        if (isListening) {
            launch {
                Whisper.detectedFrequency.collect { detection: FrequencyDetection ->
                    detectedFreq = detection.frequency
                    magnitude = detection.magnitude
                }
            }
            launch {
                Whisper.carrierEvents.collect { event ->
                    isCarrierDetected = when (event) {
                        is CarrierDetected -> true
                        CarrierLost -> false
                    }
                }
            }
            launch {
                Whisper.decodedBits.collect { bit ->
                    if (bit != -1) {
                        decodedBits += bit.toString()
                    } else {
                        decodedBits += "_"
                    }
                }
            }
            launch {
                Whisper.receivedData.collect { data ->
                    receivedText += data.decodeToString()
                }
            }
        }
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Whisper Tone Detection", style = MaterialTheme.typography.h4)

            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Carrier Status:", style = MaterialTheme.typography.h6)
                    Text(
                        if (isCarrierDetected) "DETECTED" else "NOT DETECTED",
                        style = MaterialTheme.typography.h6,
                        color = if (isCarrierDetected) MaterialTheme.colors.primary else MaterialTheme.colors.error
                    )
                }
            }

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

            OutlinedTextField(
                value = textToTransmit,
                onValueChange = { textToTransmit = it },
                label = { Text("Text to Transmit") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        Whisper.transmit(textToTransmit.encodeToByteArray())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = textToTransmit.isNotEmpty()
            ) {
                Text("Transmit Text")
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

            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Received Data:", style = MaterialTheme.typography.h6)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(receivedText, style = MaterialTheme.typography.body1)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Raw Bits (Live):", style = MaterialTheme.typography.subtitle2)
                    Text(decodedBits, style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}
