package com.whisper.doc.sections.whereto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WhereToSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
    ) {
        Text(
            text = "Where to use Whisper?",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Whisper isn't a replacement for Wi-Fi or Bluetooth; it's a specialized tool for specific environments where radio waves are impractical, unavailable, or insecure.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Best Use Cases",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        UseCaseGrid()

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Software & App Ideas",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        IdeaCard(
            title = "WhisperGate",
            description = "A contactless entry system for offices or secure zones. Instead of NFC (which requires specialized hardware), use standard phone speakers to transmit encrypted acoustic tokens to a receiver at the door."
        )

        Spacer(modifier = Modifier.height(16.dp))

        IdeaCard(
            title = "SoundPay",
            description = "Offline micro-transactions in remote areas. Customers can pay by holding their phone near the vendor's device, exchanging payment confirmation tokens via ultrasound even with zero cellular coverage."
        )

        Spacer(modifier = Modifier.height(16.dp))

        IdeaCard(
            title = "EchoPass",
            description = "A proximity-based password manager. It only auto-fills credentials on your laptop if it 'hears' a unique, rotating acoustic key being emitted by your authorized mobile device."
        )

        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
fun UseCaseGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            UseCaseItem(
                modifier = Modifier.weight(1f),
                title = "Air-Gapped Sync",
                description = "Synchronize configuration or small files between devices in EMI-sensitive or high-security zones where RF is banned."
            )
            UseCaseItem(
                modifier = Modifier.weight(1f),
                title = "Initial Handshake",
                description = "Exchange Wi-Fi or Bluetooth credentials automatically by simply being in the same room, eliminating manual pairing."
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            UseCaseItem(
                modifier = Modifier.weight(1f),
                title = "Museum Guides",
                description = "Trigger location-specific audio or descriptions on a visitor's phone using ambient ultrasound emitters near exhibits."
            )
            UseCaseItem(
                modifier = Modifier.weight(1f),
                title = "Presence Proof",
                description = "Verify that a user is physically present at a location (like a check-in desk) without relying on spoofable GPS data."
            )
        }
    }
}

@Composable
fun UseCaseItem(modifier: Modifier = Modifier, title: String, description: String) {
    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun IdeaCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
