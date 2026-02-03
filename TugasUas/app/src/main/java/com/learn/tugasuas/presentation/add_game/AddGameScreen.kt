package com.learn.tugasuas.presentation.add_game

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link // Icon untuk Link
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.data.repository.GameRepository
import com.learn.tugasuas.ui.theme.NeonPurple
import com.learn.tugasuas.ui.theme.SurfaceDark
import com.learn.tugasuas.ui.theme.TextGray
import com.learn.tugasuas.ui.theme.TextWhite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    navController: NavController,
    onSaveClick: (Game) -> Unit,
    userId: String,
    gameToEdit: Game? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { GameRepository() }

    // --- STATE DATA ---
    var title by remember { mutableStateOf(gameToEdit?.title ?: "") }

    // State untuk Gambar
    var imageUrl by remember { mutableStateOf(gameToEdit?.imageUrl ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // State untuk Link Game (Steam/Web) -> FITUR YANG DIMINTA
    var gameUrl by remember { mutableStateOf(gameToEdit?.gameUrl ?: "") }

    var rating by remember { mutableIntStateOf(gameToEdit?.rating ?: 0) }
    var notes by remember { mutableStateOf(gameToEdit?.notes ?: "") }

    // Dropdown Data
    val genreOptions = listOf("Action", "RPG", "Strategy", "FPS", "Adventure", "Sports", "Racing", "Puzzle")
    var genre by remember { mutableStateOf(if (gameToEdit != null && genreOptions.contains(gameToEdit.genre)) gameToEdit.genre else genreOptions[0]) }
    var expandedGenre by remember { mutableStateOf(false) }

    val statusOptions = listOf("Playing", "Completed", "On Hold", "Dropped", "Plan to Play")
    var status by remember { mutableStateOf(if (gameToEdit != null && statusOptions.contains(gameToEdit.status)) gameToEdit.status else statusOptions[0]) }
    var expandedStatus by remember { mutableStateOf(false) }

    // UI State
    var showSaveDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val screenTitle = if (gameToEdit != null) "Edit Game" else "Add New Game"

    // Launcher Galeri
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
                imageUrl = "Image selected from Gallery"
            }
        }
    )

    BackHandler { showDiscardDialog = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle, color = TextWhite, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonPurple)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Game Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple, unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                        focusedLabelColor = NeonPurple, unfocusedLabelColor = Color.Gray
                    )
                )

                // 2. Image URL / Gallery
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                        selectedImageUri = null
                    },
                    label = { Text("Image URL or Select from Gallery") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Icon(Icons.Default.Image, contentDescription = "Pick Image", tint = NeonPurple)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple, unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                        focusedLabelColor = NeonPurple, unfocusedLabelColor = Color.Gray
                    )
                )

                // 3. Game URL (Link Steam/Website) - FITUR BARU
                OutlinedTextField(
                    value = gameUrl,
                    onValueChange = { gameUrl = it },
                    label = { Text("Game Link (e.g. Steam / Play Store)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(Icons.Default.Link, contentDescription = "Link", tint = NeonPurple)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple, unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                        focusedLabelColor = NeonPurple, unfocusedLabelColor = Color.Gray
                    )
                )

                // 4. Genre & Status (Sejajar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Genre
                    ExposedDropdownMenuBox(
                        expanded = expandedGenre,
                        onExpandedChange = { expandedGenre = !expandedGenre },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = genre,
                            onValueChange = { },
                            label = { Text("Genre") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenre) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple, unfocusedBorderColor = Color.Gray,
                                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                                focusedLabelColor = NeonPurple, unfocusedLabelColor = Color.Gray
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedGenre,
                            onDismissRequest = { expandedGenre = false }
                        ) {
                            genreOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = { genre = option; expandedGenre = false }
                                )
                            }
                        }
                    }

                    // Status
                    ExposedDropdownMenuBox(
                        expanded = expandedStatus,
                        onExpandedChange = { expandedStatus = !expandedStatus },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = status,
                            onValueChange = { },
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple, unfocusedBorderColor = Color.Gray,
                                focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                                focusedLabelColor = NeonPurple, unfocusedLabelColor = Color.Gray
                            ),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStatus,
                            onDismissRequest = { expandedStatus = false }
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = { status = option; expandedStatus = false }
                                )
                            }
                        }
                    }
                }

                // 5. Rating
                Column {
                    Text("Rating", style = MaterialTheme.typography.labelLarge, color = NeonPurple, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rate $i",
                                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { rating = i }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$rating/5", color = TextWhite, style = MaterialTheme.typography.titleMedium)
                    }
                }

                // 6. Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple, unfocusedBorderColor = Color.Gray,
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                        focusedLabelColor = NeonPurple, unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 7. Save Button
                Button(
                    onClick = {
                        if (title.isNotEmpty() && genre.isNotEmpty()) {
                            showSaveDialog = true
                        } else {
                            Toast.makeText(context, "Title and Genre cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (gameToEdit != null) "Update Game" else "Save Game",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- DIALOGS ---
        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text(text = "Save Changes?", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to save this game data?", color = TextGray) },
                containerColor = SurfaceDark,
                icon = { Icon(Icons.Default.Check, contentDescription = null, tint = NeonPurple) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSaveDialog = false
                            isLoading = true

                            scope.launch {
                                // Logic Upload Gambar (tetap ada)
                                val finalImageUrl = if (selectedImageUri != null) {
                                    val uploadedUrl = repository.uploadImageToStorage(selectedImageUri!!)
                                    if (uploadedUrl.isNotEmpty()) uploadedUrl else imageUrl
                                } else {
                                    imageUrl
                                }

                                // Membuat Object Game dengan gameUrl
                                val idToUse = gameToEdit?.id ?: System.currentTimeMillis().toString()
                                val newGame = Game(
                                    id = idToUse,
                                    userId = userId,
                                    title = title,
                                    genre = genre,
                                    status = status,
                                    rating = rating,
                                    notes = notes,
                                    imageUrl = finalImageUrl,
                                    gameUrl = gameUrl // <--- MENYIMPAN LINK GAME
                                )

                                onSaveClick(newGame)
                                isLoading = false
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Yes, Save", color = NeonPurple, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancel", color = TextGray)
                    }
                }
            )
        }

        if (showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { showDiscardDialog = false },
                title = { Text(text = "Discard Changes?", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to leave? Unsaved changes will be lost.", color = TextGray) },
                containerColor = SurfaceDark,
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF5252)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDiscardDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Leave", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDiscardDialog = false }) {
                        Text("Stay", color = TextGray)
                    }
                }
            )
        }
    }
}