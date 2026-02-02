// alankrisnadifayana/tugas-mobile-programing/Tugas-mobile-programing-main/TugasUas/app/src/main/java/com/learn/tugasuas/presentation/home/components/GameItem.kt

package com.learn.tugasuas.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.ui.theme.CardBorderGradient
import com.learn.tugasuas.ui.theme.NeonGreen
import com.learn.tugasuas.ui.theme.NeonPurple
import com.learn.tugasuas.ui.theme.SurfaceDark
import com.learn.tugasuas.ui.theme.TextGray
import com.learn.tugasuas.ui.theme.TextWhite

@Composable
fun GameItem(
    game: Game,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        // Memberikan efek border gradasi neon
        border = BorderStroke(1.dp, CardBorderGradient)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // --- TAMPILKAN GAMBAR JIKA URL TIDAK KOSONG (BARU) ---
            if (game.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = "Game Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp) // Tinggi gambar
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- Header: Judul & Status ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.genre,
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonPurple
                    )
                }

                // Chip Status Custom
                StatusChip(status = game.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Rating & Notes ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700), // Emas
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${game.rating}/5",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }

            if (game.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${game.notes}\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))

            // --- Tombol Aksi ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = NeonPurple
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF5252) // Merah soft
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val backgroundColor = if (status == "Playing") NeonGreen.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)
    val textColor = if (status == "Playing") NeonGreen else Color.Gray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}