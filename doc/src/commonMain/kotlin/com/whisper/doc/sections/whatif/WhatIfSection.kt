package com.whisper.doc.sections.whatif

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WhatIfSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Text(
            text = "Troubleshooting & Edge Cases",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        QAItem(
            question = "What if there is loud background noise?",
            answer = "Whisper uses a bandpass filter to ignore frequencies outside the 18-22 kHz range. Most environmental noise (talking, music, traffic) is below 15 kHz. However, extremely loud metallic noises or specialized ultrasound jammers can cause interference. In these cases, increasing the FEC redundancy is recommended."
        )

        QAItem(
            question = "What if the devices are too far apart?",
            answer = "The effective range of Whisper is typically 1-5 meters depending on the speaker volume and microphone sensitivity. Sound follows the inverse square law, so signal strength drops rapidly with distance. If you need more range, you should lower the carrier frequency (closer to 17 kHz) or increase the transmission volume."
        )

        QAItem(
            question = "What if I want to send large files?",
            answer = "Whisper is optimized for low-bandwidth, high-reliability data like text, authentication tokens, or peer discovery info. Sending large files (megabytes) via sound is slow (approx. 100-500 bps). For large data, we recommend using Whisper to exchange Wi-Fi Direct or Bluetooth credentials, then switching to those high-speed channels."
        )

        QAItem(
            question = "What if a user has hearing aids?",
            answer = "Some hearing aids can amplify high-frequency sounds. While Whisper operates near the edge of human hearing, users with sensitive equipment might hear a very faint 'whistle' or 'static'. We recommend providing a toggle in your app to disable acoustic features for accessibility."
        )

        QAItem(
            question = "What if the signal reflects off walls?",
            answer = "Multipath interference is a common challenge in acoustic communication. Whisper's FSK modem includes guard intervals between symbols to allow echoes to die down before the next bit is processed, ensuring the decoder doesn't get confused by reflected waves."
        )

        QAItem(
            question = "What if a user has pet?",
            answer = "Pets such as dogs or cats can hear high-frequency sounds. While Whisper operates near the edge of human hearing, animals might hear a very faint 'whistle' or 'static'. We recommend providing a toggle in your app to disable acoustic features for accessibility."
        )

        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun QAItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 28.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Divider(modifier = Modifier.padding(top = 24.dp), color = MaterialTheme.colorScheme.outlineVariant)
    }
}
