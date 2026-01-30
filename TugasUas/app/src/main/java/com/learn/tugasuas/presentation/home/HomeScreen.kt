package com.learn.tugasuas.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.learn.tugasuas.data.UserData
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.presentation.home.components.GameItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    onAddGameClick: () -> Unit,
    onEditGameClick: (Game) -> Unit, // Callback untuk edit
    viewModel: HomeViewModel         // Terima VM dari Main
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = userData) {
        userData?.userId?.let { viewModel.loadGames(it) }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = userData?.profilePictureUrl, contentDescription = "Profile",
                        modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Welcome back, Gamer!", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                        Text(userData?.username ?: "Player", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
                IconButton(onClick = onSignOut) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGameClick, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.games.isEmpty()) {
                Text("No games yet.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(state.games) { game ->
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