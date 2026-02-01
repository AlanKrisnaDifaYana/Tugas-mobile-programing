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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.learn.tugasuas.data.UserData
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.presentation.home.components.GameItem
import com.learn.tugasuas.ui.theme.BackgroundGradient
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
    val categories = listOf("All", "Action", "RPG", "Strategy", "FPS", "Adventure")

    LaunchedEffect(key1 = userData) {
        userData?.userId?.let { viewModel.loadGames(it) }
    }

    Scaffold(
        containerColor = Color.Transparent, // Supaya gradient background terlihat
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onAddClick()
                    onAddGameClick()
                },
                containerColor = NeonPurple,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        // Container Utama dengan Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = BackgroundGradient) // Background Gradient
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // --- Header Profil ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = userData?.profilePictureUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, NeonPurple, CircleShape) // Border profil neon
                                .background(SurfaceDark),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Hello, Gamer!",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextGray
                            )
                            Text(
                                userData?.username ?: "Player 1",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = Color(0xFFFF5252))
                    }
                }

                // --- Search Bar ---
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    placeholder = { Text("Search games...", color = TextGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonPurple) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        cursorColor = NeonPurple,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark.copy(alpha = 0.5f)
                    )
                )

                // --- Category Filter ---
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
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
                                selectedContainerColor = NeonPurple,
                                selectedLabelColor = Color.Black,
                                containerColor = SurfaceDark,
                                labelColor = TextGray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if(isSelected) Color.Transparent else Color.White.copy(alpha = 0.2f),
                                enabled = true,
                                selected = isSelected
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- Game List ---
                Box(modifier = Modifier.fillMaxSize()) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = NeonPurple
                        )
                    } else if (state.games.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No games found",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextGray
                            )
                            Text(
                                "Add a new game to start tracking!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = state.games,
                                key = { it.id } // Penting untuk performa list
                            ) { game ->
                                GameItem(
                                    game = game,
                                    onDeleteClick = { viewModel.deleteGame(game.id) },
                                    onEditClick = { onEditGameClick(game) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}