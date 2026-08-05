package com.whisper.doc.sections.howto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HowToSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Text(
            text = "Integration Guide",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        StepCard(
            number = "1",
            title = "Initialization",
            description = "The first step is to configure the Whisper engine. You can adjust the sample rate and carrier frequencies to match your environment's acoustic properties.",
            code = "val config = WhisperConfig(\n" +
                   "    sampleRate = 48000,\n" +
                   "    carrierFrequency = 19000f,\n" +
                   "    fecConfig = FecConfig(enabled = true, redundancy = 2)\n" +
                   ")\n\n" +
                   "Whisper.configure(config)"
        )

        Spacer(modifier = Modifier.height(24.dp))

        StepCard(
            number = "2",
            title = "Receiving Data",
            description = "To start listening, call startListening(). Use the receivedData flow to collect decoded byte arrays. Remember to handle permissions (RECORD_AUDIO) on mobile platforms.",
            code = "// Start detection engine\n" +
                   "Whisper.startListening()\n\n" +
                   "// Collect data\n" +
                   "scope.launch {\n" +
                   "    Whisper.receivedData.collect { data ->\n" +
                   "        println(\"Received bytes: \${data.size}\")\n" +
                   "    }\n" +
                   "}"
        )

        Spacer(modifier = Modifier.height(24.dp))

        StepCard(
            number = "3",
            title = "Transmitting Data",
            description = "Transmitting is as simple as calling transmit with a byte array. This function handles the FSK encoding and audio playback internally.",
            code = "val msg = \"Hello\".encodeToByteArray()\n" +
                   "scope.launch {\n" +
                   "    Whisper.transmit(msg)\n" +
                   "}"
        )

        Spacer(modifier = Modifier.height(24.dp))

        StepCard(
            number = "4",
            title = "Monitoring Events",
            description = "Observe the carrierEvents flow to detect when a transmitter is nearby, even if data isn't being sent yet.",
            code = "Whisper.carrierEvents.collect { event ->\n" +
                   "    when(event) {\n" +
                   "        is CarrierDetected -> showActiveUI()\n" +
                   "        is CarrierLost -> hideActiveUI()\n" +
                   "    }\n" +
                   "}"
        )

        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun StepCard(number: String, title: String, description: String, code: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(number, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    color = Color(0xFF81C784)
                )
            }
        }
    }
}
