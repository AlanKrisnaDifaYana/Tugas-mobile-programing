package com.learn.tugasuas.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link // Import icon Link
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.ui.theme.NeonGreen
import com.learn.tugasuas.ui.theme.NeonPurple
import com.learn.tugasuas.ui.theme.NeonBlue
import com.learn.tugasuas.ui.theme.TextWhite

@Composable
fun GameItem(
    game: Game,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onUrlClick: (String) -> Unit, // Parameter baru untuk aksi buka link
    modifier: Modifier = Modifier
) {
    // Tetap menggunakan UI yang sudah ada (Glowing border)
    val cardModifier = if (game.status == "Playing") {
        modifier
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(NeonGreen, NeonBlue)),
                shape = RoundedCornerShape(24.dp)
            )
    } else {
        modifier
    }

    Card(
        modifier = cardModifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (game.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = game.imageUrl,
                    contentDescription = "Game Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF2D2D2D)))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.95f)
                            ),
                            startY = 50f
                        )
                    )
            )

            Box(modifier = Modifier.padding(16.dp).align(Alignment.TopStart)) {
                StatusChip(status = game.status)
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = game.rating.toString(),
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = game.genre.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (game.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${game.notes}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite.copy(alpha = 0.8f),
                        fontStyle = FontStyle.Italic,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // --- TOMBOL URL (LINK) BARU ---
                    if (game.gameUrl.isNotEmpty()) {
                        IconButton(
                            onClick = { onUrlClick(game.gameUrl) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Link,
                                contentDescription = "Open Link",
                                tint = NeonBlue, // Warna biru neon agar terlihat kontras
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextWhite.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val backgroundColor = if (status == "Playing") NeonGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.2f)
    val textColor = if (status == "Playing") NeonGreen else Color.LightGray
    val borderColor = if (status == "Playing") NeonGreen.copy(alpha = 0.5f) else Color.Transparent

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
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