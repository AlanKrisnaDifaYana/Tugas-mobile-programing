package com.learn.tugasuas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Kita definisikan skema warna Gelap (Gamer Theme)
private val GamerColorScheme = darkColorScheme(
    primary = NeonPurple,
    secondary = NeonGreen,
    tertiary = NeonPurple,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite,
)

@Composable
fun TugasUasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // MATIKAN dynamic color agar tema Neon kita tidak tertimpa
    content: @Composable () -> Unit
) {
    val colorScheme = GamerColorScheme // Selalu gunakan tema gelap

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}