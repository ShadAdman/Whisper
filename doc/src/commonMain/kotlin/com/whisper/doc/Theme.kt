package com.whisper.doc

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import whisper.doc.generated.resources.Res
import whisper.doc.generated.resources.LatoRegular
import whisper.doc.generated.resources.LatoBold

@Composable
fun WhisperTheme(content: @Composable () -> Unit) {
    val latoFont = FontFamily(
        Font(Res.font.LatoRegular, FontWeight.Normal),
        Font(Res.font.LatoBold, FontWeight.Bold)
    )
    
    val grayWhite = Color(0xFFE0E0E0)
    val mutedGray = Color(0xFFB0B0B0)

    val darkColorScheme = darkColorScheme(
        primary = Color(0xFF64B5F6),
        secondary = Color(0xFF81C784),
        background = Color.Black,
        surface = Color.Black,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = grayWhite,
        onSurface = grayWhite,
        onSurfaceVariant = mutedGray,
        outlineVariant = Color(0xFF333333)
    )

    MaterialTheme(
        colorScheme = darkColorScheme,
        typography = Typography(
            displayLarge = TextStyle(fontFamily = latoFont, fontWeight = FontWeight.Bold, fontSize = 96.sp, letterSpacing = (-2).sp),
            displayMedium = TextStyle(fontFamily = latoFont, fontWeight = FontWeight.Bold),
            headlineLarge = TextStyle(fontFamily = latoFont, fontWeight = FontWeight.Bold),
            headlineSmall = TextStyle(fontFamily = latoFont, fontWeight = FontWeight.Normal, fontSize = 28.sp, lineHeight = 40.sp),
            titleLarge = TextStyle(fontFamily = latoFont, fontWeight = FontWeight.Bold, fontSize = 20.sp),
            labelLarge = TextStyle(fontFamily = latoFont, fontWeight = FontWeight.Normal),
            bodyLarge = TextStyle(fontFamily = latoFont, fontWeight = FontWeight.Normal),
            bodyMedium = TextStyle(fontFamily = latoFont, fontWeight = FontWeight.Normal),
        ),
        content = content
    )
}
