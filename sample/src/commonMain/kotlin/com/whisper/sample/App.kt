package com.whisper.sample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whisper.api.Whisper
import com.whisper.config.WhisperConfig
import com.whisper.core.error.FecConfig
import com.whisper.core.protocol.ProtocolType
import com.whisper.core.model.CarrierDetected
import com.whisper.core.model.CarrierLost
import com.whisper.core.model.FrequencyDetection
import com.whisper.core.packet.InvalidPacket
import com.whisper.core.packet.ValidPacket
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
    var lastPacketStatus by remember { mutableStateOf<String?>(null) }
    var nearbyDevices by remember { mutableStateOf(setOf<String>()) }
    
    val scrollState = rememberScrollState()
    
    var isFecEnabled by remember { mutableStateOf(true) }
    var redundancy by remember { mutableStateOf(3) }
    var selectedProtocol by remember { mutableStateOf(ProtocolType.DEFAULT) }
    

    LaunchedEffect(isFecEnabled, redundancy, selectedProtocol) {
        Whisper.configure(
            WhisperConfig(
                fecConfig = FecConfig(enabled = isFecEnabled, redundancy = redundancy),
                protocolType = selectedProtocol
            )
        )
    }

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
                    if (decodedBits.length > 100) decodedBits = decodedBits.takeLast(100)
                }
            }
            launch {
                Whisper.packetResults.collect { result ->
                    when (result) {
                        is ValidPacket -> {
                            val packet = result.packet
                            val payloadText = packet.payload.decodeToString()
                            receivedText += "\n[Packet] v${packet.version} type=${packet.type} len=${packet.payload.size}: $payloadText"
                            nearbyDevices = nearbyDevices + payloadText
                            lastPacketStatus = "CRC PASS (${packet.payload.size} bytes)"
                        }
                        InvalidPacket -> {
                            receivedText += "\n[CORRUPTED PACKET]"
                            lastPacketStatus = "CRC FAILED"
                        }
                    }
                }
            }
        }
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Whisper Example", style = MaterialTheme.typography.h4)

            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Carrier Status:", style = MaterialTheme.typography.h6)
                        Text(
                            if (isCarrierDetected) "DETECTED" else "NOT DETECTED",
                            style = MaterialTheme.typography.h6,
                            color = if (isCarrierDetected) MaterialTheme.colors.primary else MaterialTheme.colors.error
                        )
                    }
                    Divider()
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(checked = isFecEnabled, onCheckedChange = { isFecEnabled = it })
                        Text("Enable FEC (Repetition)")
                    }
                    if (isFecEnabled) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text("Redundancy: $redundancy")
                            Slider(
                                value = redundancy.toFloat(),
                                onValueChange = { redundancy = it.toInt() },
                                valueRange = 1f..7f,
                                steps = 5,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                    Divider()
                    Text("Protocol:", style = MaterialTheme.typography.subtitle1)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedProtocol == ProtocolType.DEFAULT,
                            onClick = { selectedProtocol = ProtocolType.DEFAULT }
                        )
                        Text("Default (FSK)")
                        Spacer(Modifier.width(16.dp))
                        RadioButton(
                            selected = selectedProtocol == ProtocolType.MORSE,
                            onClick = { selectedProtocol = ProtocolType.MORSE }
                        )
                        Text("Morse Code")
                    }
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

            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text("Nearby Devices", style = MaterialTheme.typography.h6)
                        if (isCarrierDetected) {
                            Spacer(Modifier.width(8.dp))
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    if (nearbyDevices.isEmpty()) {
                        Text(
                            text = if (isCarrierDetected) "Signal detected, waiting for ID..." else "Searching for nearby devices...",
                            style = MaterialTheme.typography.caption,
                            color = if (isCarrierDetected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )
                    } else {
                        nearbyDevices.forEach { name ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Text(name, style = MaterialTheme.typography.body1)
                                Text("Online", color = MaterialTheme.colors.primary, style = MaterialTheme.typography.overline)
                            }
                        }
                    }
                }
            }

            Card(elevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Received Data:", style = MaterialTheme.typography.h6)
                    lastPacketStatus?.let {
                        Text(it, style = MaterialTheme.typography.caption, color = if (it.contains("PASS")) MaterialTheme.colors.primary else MaterialTheme.colors.error)
                    }
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
