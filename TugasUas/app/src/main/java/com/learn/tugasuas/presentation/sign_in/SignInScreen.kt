package com.learn.tugasuas.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn.tugasuas.ui.theme.BackgroundDark
import com.learn.tugasuas.ui.theme.NeonBlue
import com.learn.tugasuas.ui.theme.NeonPurple
import com.learn.tugasuas.ui.theme.TextGray
import com.learn.tugasuas.ui.theme.TextWhite

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    // Menampilkan pesan error jika login gagal
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark) // Menggunakan BackgroundDark dari Color.kt
    ) {
        // --- DEKORASI LATAR BELAKANG (Efek Glow) ---
        // Glow ungu di atas
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-80).dp)
                .size(300.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        // Glow biru di bawah
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .size(350.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonBlue.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
        )

        // --- KONTEN UTAMA ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ikon Gamepad dengan efek neon
            Icon(
                imageVector = Icons.Default.Gamepad,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = NeonPurple // Menggunakan NeonPurple
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Judul Aplikasi dengan gaya futuristik
            Text(
                text = "GAME COLLECTION",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite, // Menggunakan TextWhite
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Ultimate Game Library Tracker",
                fontSize = 14.sp,
                color = TextGray, // Menggunakan TextGray
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Tombol Login dengan gaya Glassmorphism & Border Gradient
            Button(
                onClick = onSignInClick, // Aksi ketika tombol diklik
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.08f) // Efek transparan
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(NeonPurple, NeonBlue)) // Border gradient
                )
            ) {
                Text(
                    text = "Sign in with Google",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer / Keterangan tambahan
            Text(
                text = "Start your journey by signing in with your Google account.",
                fontSize = 12.sp,
                color = TextGray.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}