package com.learn.tugasuas.presentation.add_game

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel // <--- Parameter ini KUNCI agar MainActivity tidak merah
) {
    val context = LocalContext.current
    val gameToEdit = viewModel.gameToEdit
    val isEditMode = gameToEdit != null

    var title by remember { mutableStateOf(gameToEdit?.title ?: "") }
    var selectedGenre by remember { mutableStateOf(gameToEdit?.genre ?: "Action") }
    var selectedStatus by remember { mutableStateOf(gameToEdit?.status ?: "Playing") }
    var rating by remember { mutableIntStateOf(gameToEdit?.rating ?: 3) }
    var notes by remember { mutableStateOf(gameToEdit?.notes ?: "") }

    val genres = listOf("Action", "RPG", "FPS", "Puzzle", "Sports", "Adventure")
    val statuses = listOf("Backlog", "Playing", "Finished", "Dropped")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Game" else "Tambah Game Baru") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // Form Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Game") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Status Chips
            Text("Status:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statuses.forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status },
                        label = { Text(status) },
                        leadingIcon = if (selectedStatus == status) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Genre Chips
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { selectedGenre = genre },
                        label = { Text(genre) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Rating
            Text("Rating: $rating/5", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                (1..5).forEach { star ->
                    IconButton(onClick = { rating = star }) {
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "Star $star",
                            tint = if (star <= rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Catatan / Review Singkat") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "Judul tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    } else {
                        if (isEditMode) {
                            val updatedGame = gameToEdit!!.copy(
                                title = title, status = selectedStatus, genre = selectedGenre, rating = rating, notes = notes
                            )
                            viewModel.updateGame(updatedGame)
                            Toast.makeText(context, "Berhasil update!", Toast.LENGTH_SHORT).show()
                        } else {
                            val newGame = Game(
                                userId = userId, title = title, status = selectedStatus, genre = selectedGenre, rating = rating, notes = notes
                            )
                            viewModel.addGame(newGame)
                            Toast.makeText(context, "Berhasil simpan!", Toast.LENGTH_SHORT).show()
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Update Game" else "Simpan ke Jurnal")
            }
        }
    }
}