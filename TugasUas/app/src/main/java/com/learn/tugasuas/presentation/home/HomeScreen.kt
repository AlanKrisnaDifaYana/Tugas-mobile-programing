package com.learn.tugasuas.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.learn.tugasuas.data.UserData
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.presentation.home.components.GameItem
import com.learn.tugasuas.ui.theme.BackgroundDark
import com.learn.tugasuas.ui.theme.NeonBlue
import com.learn.tugasuas.ui.theme.NeonPurple
import com.learn.tugasuas.ui.theme.SurfaceDark
import com.learn.tugasuas.ui.theme.TextGray
import com.learn.tugasuas.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    onAddGameClick: () -> Unit,
    onEditGameClick: (Game) -> Unit,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val categories = listOf("All", "Action", "RPG", "Strategy", "FPS", "Adventure", "Sports", "Racing", "Puzzle")

    // --- STATE UNTUK DIALOG DELETE ---
    // Menyimpan game mana yang sedang ingin dihapus. Jika null, dialog tidak muncul.
    var gameToDelete by remember { mutableStateOf<Game?>(null) }

    LaunchedEffect(key1 = userData) {
        userData?.userId?.let { viewModel.loadGames(it) }
    }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            // Glowing FAB
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonPurple.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
                FloatingActionButton(
                    onClick = {
                        viewModel.onAddClick()
                        onAddGameClick()
                    },
                    containerColor = NeonPurple,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Background Blobs
            Box(
                modifier = Modifier.offset(x = (-80).dp, y = (-80).dp).size(300.dp)
                    .background(Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent)))
            )
            Box(
                modifier = Modifier.align(Alignment.CenterEnd).offset(x = 100.dp, y = 100.dp).size(350.dp)
                    .background(Brush.radialGradient(listOf(NeonBlue.copy(alpha = 0.15f), Color.Transparent)))
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Header Profil
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = userData?.profilePictureUrl, contentDescription = "Profile",
                            modifier = Modifier.size(50.dp).clip(CircleShape).border(2.dp, NeonPurple, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Welcome back,", style = MaterialTheme.typography.labelMedium, color = TextGray)
                            Text(userData?.username ?: "Player", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape).size(40.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = Color(0xFFFF5252))
                    }
                }

                // Search Bar
                TextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(100.dp)),
                    placeholder = { Text("Find your next game...", color = TextGray.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonPurple) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f), unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, cursorColor = NeonPurple
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Filter
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = state.selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCategorySelected(category) },
                            label = { Text(category) },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonPurple, selectedLabelColor = Color.White,
                                containerColor = SurfaceDark, labelColor = TextGray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isSelected) NeonPurple else Color.White.copy(alpha = 0.2f),
                                enabled = true, selected = isSelected
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Game List
                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NeonPurple)
                    } else if (state.games.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(100.dp), tint = TextGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No Games Found", style = MaterialTheme.typography.titleLarge, color = TextWhite, fontWeight = FontWeight.Bold)
                            Text("Start your collection by adding a new game!", style = MaterialTheme.typography.bodyMedium, color = TextGray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(items = state.games, key = { it.id }) { game ->
                                GameItem(
                                    game = game,
                                    // SAAT TOMBOL DELETE DI KLIK, JANGAN LANGSUNG HAPUS.
                                    // TAPI SIMPAN GAME KE VARIABEL 'gameToDelete' UNTUK MEMUNCULKAN DIALOG
                                    onDeleteClick = { gameToDelete = game },
                                    onEditClick = { onEditGameClick(game) }
                                )
                            }
                        }
                    }
                }
            }

            // --- DIALOG KONFIRMASI DELETE ---
            if (gameToDelete != null) {
                AlertDialog(
                    onDismissRequest = { gameToDelete = null },
                    title = { Text(text = "Delete Game?", color = TextWhite, fontWeight = FontWeight.Bold) },
                    text = {
                        Text("Are you sure you want to delete \"${gameToDelete?.title}\"? This action cannot be undone.", color = TextGray)
                    },
                    containerColor = SurfaceDark,
                    icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // Hapus game
                                gameToDelete?.id?.let { viewModel.deleteGame(it) }
                                gameToDelete = null // Tutup dialog
                            }
                        ) {
                            Text("Yes, Delete", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { gameToDelete = null }) {
                            Text("Cancel", color = TextGray)
                        }
                    }
                )
            }
        }
    }
}