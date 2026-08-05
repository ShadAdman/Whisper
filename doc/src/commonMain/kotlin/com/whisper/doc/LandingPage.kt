package com.whisper.doc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import whisper.doc.generated.resources.Res
import whisper.doc.generated.resources.whisper_logo

@Composable
fun LandingPage(onGetStarted: () -> Unit) {
    val scrollState = rememberScrollState()
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isMobile = maxWidth < 800.dp
        val screenHeight = maxHeight
        
        Box(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            if (isMobile) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animation on top for mobile
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        WhisperAnimation(modifier = Modifier.fillMaxSize())
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "ACOUSTIC DATA TRANSFER",
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Image(
                        painter = painterResource(Res.drawable.whisper_logo),
                        contentDescription = "Whisper Logo",
                        modifier = Modifier.size(80.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Whisper",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "A secure, decentralized, and ultra-low-latency communication protocol using ultrasound acoustic waves. Whisper allows devices to detect each other and exchange data without Wi-Fi, Bluetooth, or cellular networks.",
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp, lineHeight = 32.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = onGetStarted,
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            "Get Started",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = screenHeight)
                        .padding(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side: Text Content
                    Column(
                        modifier = Modifier
                            .weight(0.6f)
                            .padding(horizontal = 48.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.whisper_logo),
                            contentDescription = "Whisper Logo",
                            modifier = Modifier.size(160.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Whisper",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "A secure, decentralized, and ultra-low-latency communication protocol using ultrasound acoustic waves. Whisper allows devices to detect each other and exchange data without Wi-Fi, Bluetooth, or cellular networks.",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(64.dp))
                        Button(
                            onClick = onGetStarted,
                            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                "Get Started",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black
                            )
                        }
                    }

                    // Right Side: Animation
                    Column(
                        modifier = Modifier
                            .weight(0.4f)
                            .height(screenHeight)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            WhisperAnimation(modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "ACOUSTIC DATA TRANSFER",
                            style = MaterialTheme.typography.labelLarge.copy(
                                letterSpacing = 6.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
