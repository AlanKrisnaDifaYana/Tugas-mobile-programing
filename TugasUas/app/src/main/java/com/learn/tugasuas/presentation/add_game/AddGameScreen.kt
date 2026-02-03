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
import androidx.compose.material.icons.filled.Image // Icon Galeri
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
    // Context & Scope
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Repository (Sebaiknya di-inject via ViewModel, tapi untuk tugas ini kita instansiasi disini)
    val repository = remember { GameRepository() }

    // State Data Form
    var title by remember { mutableStateOf(gameToEdit?.title ?: "") }

    // Logic Gambar: Kita butuh 2 state. Satu untuk text URL, satu untuk Uri lokal (dari galeri)
    var imageUrl by remember { mutableStateOf(gameToEdit?.imageUrl ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var rating by remember { mutableIntStateOf(gameToEdit?.rating ?: 0) }
    var notes by remember { mutableStateOf(gameToEdit?.notes ?: "") }

    val genreOptions = listOf("Action", "RPG", "Strategy", "FPS", "Adventure", "Sports", "Racing", "Puzzle")
    var genre by remember { mutableStateOf(if (gameToEdit != null && genreOptions.contains(gameToEdit.genre)) gameToEdit.genre else genreOptions[0]) }
    var expandedGenre by remember { mutableStateOf(false) }

    val statusOptions = listOf("Playing", "Completed", "On Hold", "Dropped", "Plan to Play")
    var status by remember { mutableStateOf(if (gameToEdit != null && statusOptions.contains(gameToEdit.status)) gameToEdit.status else statusOptions[0]) }
    var expandedStatus by remember { mutableStateOf(false) }

    // State UI
    var showSaveDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) } // State Loading

    val screenTitle = if (gameToEdit != null) "Edit Game" else "Add New Game"

    // Launcher untuk membuka Galeri Foto
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
                // Kita update text field agar user tahu gambar terpilih (bisa diganti nama file)
                imageUrl = "Image selected from Gallery"
            }
        }
    )

    // Handle tombol Back
    BackHandler {
        showDiscardDialog = true
    }

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
        // Jika sedang loading upload, tampilkan indikator
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
                // Input Title
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

                // Input Image URL dengan Tombol Galeri
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = {
                        imageUrl = it
                        selectedImageUri = null // Reset URI jika user mengetik manual
                    },
                    label = { Text("Image URL or Select from Gallery") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            // Buka Galeri (Hanya Image)
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

                // Genre Dropdown
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

                // Status Dropdown
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

                // Rating
                Column {
                    Text("Rating", style = MaterialTheme.typography.labelLarge, color = NeonPurple, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rate $i",
                                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp).clickable { rating = i }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$rating/5", color = TextWhite, style = MaterialTheme.typography.titleMedium)
                    }
                }

                // Notes
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

                // Tombol SAVE
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
                    Text(if (gameToEdit != null) "Update Game" else "Save Game", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- DIALOGS ---

        // Dialog Save
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
                            isLoading = true // Mulai Loading

                            scope.launch {
                                // Logika Upload Gambar
                                val finalImageUrl = if (selectedImageUri != null) {
                                    // Jika user memilih gambar dari galeri, upload dulu
                                    val uploadedUrl = repository.uploadImageToStorage(selectedImageUri!!)
                                    if (uploadedUrl.isNotEmpty()) {
                                        uploadedUrl
                                    } else {
                                        // Handle error upload
                                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                                        imageUrl // Gunakan nilai lama/teks
                                    }
                                } else {
                                    // Jika tidak ada gambar baru dari galeri, pakai string yang ada di text field
                                    imageUrl
                                }

                                // Simpan Game Data
                                val idToUse = gameToEdit?.id ?: System.currentTimeMillis().toString()
                                val newGame = Game(
                                    id = idToUse,
                                    userId = userId,
                                    title = title,
                                    genre = genre,
                                    status = status,
                                    rating = rating,
                                    notes = notes,
                                    imageUrl = finalImageUrl // Pakai URL hasil upload atau input manual
                                )

                                onSaveClick(newGame) // Panggil callback simpan (ke ViewModel/Repository)
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

        // Dialog Discard (Back)
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