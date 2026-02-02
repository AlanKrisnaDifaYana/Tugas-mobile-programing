// alankrisnadifayana/tugas-mobile-programing/Tugas-mobile-programing-main/TugasUas/app/src/main/java/com/learn/tugasuas/presentation/add_game/AddGameScreen.kt

package com.learn.tugasuas.presentation.add_game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.ui.theme.NeonPurple
import com.learn.tugasuas.ui.theme.SurfaceDark
import com.learn.tugasuas.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    navController: NavController,
    onSaveClick: (Game) -> Unit,
    userId: String
) {
    // State untuk input form
    var title by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") } // State baru untuk Image URL

    // --- GANTI DI SINI: Rating kembali menjadi Integer (0) ---
    var rating by remember { mutableIntStateOf(0) }

    var notes by remember { mutableStateOf("") }

    // --- BAGIAN DROP DOWN GENRE ---
    val genreOptions = listOf("Action", "RPG", "Strategy", "FPS", "Adventure", "Sports", "Racing", "Puzzle")
    var genre by remember { mutableStateOf(genreOptions[0]) }
    var expandedGenre by remember { mutableStateOf(false) }

    // --- BAGIAN DROP DOWN STATUS ---
    val statusOptions = listOf("Playing", "Completed", "On Hold", "Dropped", "Plan to Play")
    var status by remember { mutableStateOf(statusOptions[0]) }
    var expandedStatus by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Game", color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Input Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Game Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedLabelColor = NeonPurple,
                    unfocusedLabelColor = Color.Gray
                )
            )

            // 1.5 Input Image URL (BARU)
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedLabelColor = NeonPurple,
                    unfocusedLabelColor = Color.Gray
                )
            )

            // 2. Input Genre (DROP DOWN)
            ExposedDropdownMenuBox(
                expanded = expandedGenre,
                onExpandedChange = { expandedGenre = !expandedGenre },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = genre,
                    onValueChange = { },
                    label = { Text("Genre") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenre) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = NeonPurple,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedGenre,
                    onDismissRequest = { expandedGenre = false }
                ) {
                    genreOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                genre = option
                                expandedGenre = false
                            }
                        )
                    }
                }
            }

            // 3. Input Status (DROP DOWN)
            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = status,
                    onValueChange = { },
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = NeonPurple,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                status = option
                                expandedStatus = false
                            }
                        )
                    }
                }
            }

            // 4. Input Rating (STAR INTERACTIVE)
            Column {
                Text(
                    text = "Rating",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonPurple,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rate $i",
                            tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.5f), // Emas jika aktif
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = i } // Klik untuk ubah rating
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$rating/5",
                        color = TextWhite,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // 5. Input Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedLabelColor = NeonPurple,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Save
            Button(
                onClick = {
                    if (title.isNotEmpty() && genre.isNotEmpty()) {
                        val newGame = Game(
                            id = System.currentTimeMillis().toString(),
                            userId = userId,
                            title = title,
                            genre = genre,
                            status = status,
                            rating = rating, // Menggunakan nilai Int langsung
                            notes = notes,
                            imageUrl = imageUrl // Menyimpan URL gambar ke object Game
                        )
                        onSaveClick(newGame)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Game", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}