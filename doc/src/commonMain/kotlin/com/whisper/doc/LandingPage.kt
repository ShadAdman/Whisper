package com.whisper.doc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import whisper.doc.generated.resources.Res
import whisper.doc.generated.resources.LatoRegular

@Composable
fun LandingPage() {
    val latoFont = FontFamily(Font(Res.font.LatoRegular))
    
    val darkColorScheme = darkColorScheme(
        primary = Color(0xFF64B5F6),
        secondary = Color(0xFF81C784),
        background = Color.Black,
        surface = Color.Black,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White,
        onSurfaceVariant = Color.LightGray
    )

    MaterialTheme(
        colorScheme = darkColorScheme,
        typography = Typography(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = latoFont),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = latoFont),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = latoFont),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = latoFont),
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Text Content
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(end = 48.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Whisper",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 84.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "A secure, decentralized, and ultra-low-latency communication protocol using near-ultrasound acoustic waves. Whisper allows devices to detect each other and exchange data without Wi-Fi, Bluetooth, or cellular networks.",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.height(56.dp))
                    Button(
                        onClick = { /* TODO: Get Started */ },
                        contentPadding = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            "Get Started",
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                }

                // Right Side: Animation
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.2f),
                        contentAlignment = Alignment.Center
                    ) {
                        WhisperAnimation(modifier = Modifier.fillMaxSize())
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Acoustic Data Transfer in Action",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
