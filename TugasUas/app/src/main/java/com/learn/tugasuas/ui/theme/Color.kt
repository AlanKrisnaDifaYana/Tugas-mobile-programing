package com.learn.tugasuas.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Warna Utama (Neon)
val NeonPurple = Color(0xFFD0BCFF)
val NeonGreen = Color(0xFF66FFC2)
val NeonBlue = Color(0xFF4FC3F7) // Tambahan warna biru neon

// Warna Background
val BackgroundDark = Color(0xFF0F0F1A) // Lebih gelap, kebiruan
val SurfaceDark = Color(0xFF1C1C2E)    // Warna kartu

val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFAAAAAA)

// Brush / Gradasi untuk Background & Border
val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF0F0F1A), Color(0xFF1A1A2E))
)

val CardBorderGradient = Brush.linearGradient(
    colors = listOf(NeonPurple, NeonBlue)
)

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(NeonPurple, NeonBlue)
)