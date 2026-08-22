package com.whisper.doc.sections.whatis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

@Composable
fun WhatIsSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Text(
            text = "What is Whisper?",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Whisper is a proximity-based communication protocol that enables decentralized data exchange using acoustic waves. It bypasses traditional networking hardware by using the device's existing microphones and speakers as modems.",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        SectionHeader("1. Acoustic Communication")
        Text(
            text = "Whisper operates in the near-ultrasound frequency range, typically between 18 kHz and 22 kHz. These frequencies are generally inaudible to adults but can be captured by standard consumer electronics. This creates a 'silent' network layer that is naturally air-gapped from Wi-Fi and Bluetooth.",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        AcousticWaveGraph()

        Spacer(modifier = Modifier.height(48.dp))

        SectionHeader("2. FSK Modulation (Frequency Shift Keying)")
        Text(
            text = "To represent binary data, Whisper uses FSK. Each bit is mapped to a specific frequency. For example, a '0' bit might be 18,500 Hz, and a '1' bit might be 19,500 Hz. By rapidly switching these frequencies, we encode a digital stream into a continuous sound wave.",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        FSKGraph()

        Spacer(modifier = Modifier.height(48.dp))

        SectionHeader("3. Morse Code Protocol")
        Text(
            text = "Whisper includes an optional Morse code protocol for low-bandwidth, high-reliability scenarios. Since Morse code is inherently text-based, binary data is converted to a Hexadecimal string before transmission. While slower than FSK, Morse code can be easier to integrate with legacy equipment or for use in extremely noisy environments.",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        SectionHeader("4. The DSP Pipeline")
        Text(
            text = "Acoustic signals are prone to interference. Our Digital Signal Processing (DSP) pipeline handles:\n\n" +
                 "• Windowing: Segmenting audio for FFT analysis.\n" +
                 "• Bandpass Filtering: Stripping out background noise (speech, music).\n" +
                 "• Gain Control: Normalizing signal strength for varying distances.",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        SectionHeader("5. Powered by Liquid DSP")
        Text(
            text = "Whisper's engine is built upon Liquid DSP (github.com/jgaeddert/liquid-dsp), a powerful, open-source software-defined radio library. While it is a comprehensive suite, we chose it specifically for its performance and future-readiness. Using Liquid DSP allows Whisper to:\n\n" +
                 "• Leverage Industrial-Grade Filters: Achieving sharp frequency separation that keeps the signal clear in noisy environments.\n" +
                 "• Rapidly Evolve: We can easily add support for advanced modulations like PSK or OFDM in future updates.\n" +
                 "• Cross-Platform Performance: The C-based core ensures consistent, high-speed processing across Android, iOS, and Desktop.",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        SectionHeader("6. Reliability & FEC")
        Text(
            text = "Sound reflects off walls and is absorbed by objects. Whisper uses Forward Error Correction (FEC) to ensure data integrity. By adding mathematical redundancy, the receiver can reconstruct the original message even if parts of the sound signal were corrupted by a loud noise.",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        SectionHeader("7. Secure Payload")
        Text(
            text = "Whisper includes a built-in cryptographic layer through the whisper-crypto module. It provides platform-native AES encryption to ensure that even if someone records the acoustic signal, they cannot access the underlying data without the secret key.",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )
        
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun AcousticWaveGraph() {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height
            val points = 100
            val path = Path()
            
            for (i in 0..points) {
                val x = i.toFloat() / points * width
                val y = height / 2 + sin(i.toFloat() * 0.2f) * (height / 3)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            
            drawPath(path, color = Color(0xFF64B5F6), style = Stroke(width = 4.dp.toPx()))
        }
    }
}

@Composable
fun FSKGraph() {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height
            val path = Path()
            
            var x = 0f
            val bitWidth = width / 8
            for (i in 0 until 8) {
                val freq = if (i % 2 == 0) 0.5f else 0.2f
                for (step in 0..20) {
                    val px = x + (step.toFloat() / 20) * bitWidth
                    val py = height / 2 + sin(step.toFloat() * freq * 10f) * (height / 3)
                    if (x == 0f && step == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                x += bitWidth
            }
            
            drawPath(path, color = Color(0xFF81C784), style = Stroke(width = 4.dp.toPx()))
        }
    }
}
