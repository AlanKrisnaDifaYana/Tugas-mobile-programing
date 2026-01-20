package com.learn.tugas11.presentation.todo

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.learn.tugas11.data.model.Priority
import com.learn.tugas11.data.model.Todo
import com.learn.tugas11.data.model.UserData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        topBar = {
            // TAMPILAN TOP BAR YANG LEBIH MENARIK
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Todo List",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                actions = {
                    userData?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = it.profilePictureUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .shadow(4.dp, CircleShape)
                            )
                            IconButton(
                                onClick = onSignOut,
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                // Gunakan Icons.Outlined.Logout untuk menghindari deprecation warning
                                Icon(
                                    Icons.Outlined.Logout,
                                    contentDescription = "Sign Out",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // CARD INPUT TUGAS BARU
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // INPUT SINGLE LINE
                    OutlinedTextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Apa yang ingin kamu lakukan?") },
                        singleLine = true, // TUGAS 3: SINGLE LINE
                        maxLines = 1,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (todoText.isNotEmpty()) {
                                        userData?.userId?.let { userId ->
                                            viewModel.add(userId, todoText, selectedPriority)
                                            todoText = ""
                                        }
                                    }
                                },
                                enabled = todoText.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = if (todoText.isNotEmpty()) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // PRIORITY SELECTOR
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Priority:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Priority.values().forEach { priority ->
                            FilterChip(
                                selected = selectedPriority == priority,
                                onClick = { selectedPriority = priority },
                                label = {
                                    Text(
                                        priority.name,
                                        fontSize = 12.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (priority) {
                                            Priority.LOW -> Icons.Default.LowPriority
                                            Priority.MEDIUM -> Icons.Default.Circle
                                            Priority.HIGH -> Icons.Default.Warning
                                        },
                                        contentDescription = null
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (priority) {
                                        Priority.LOW -> Color(0xFF4CAF50)
                                        Priority.MEDIUM -> Color(0xFF2196F3)
                                        Priority.HIGH -> Color(0xFFF44336)
                                    },
                                    selectedLabelColor = Color.White,
                                    iconColor = when (priority) {
                                        Priority.LOW -> Color(0xFF4CAF50)
                                        Priority.MEDIUM -> Color(0xFF2196F3)
                                        Priority.HIGH -> Color(0xFFF44336)
                                    }
                                )
                            )
                        }
                    }
                }
            }

            // ANIMASI UNTUK LIST TODO
            AnimatedVisibility(
                visible = todos.isNotEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    "Total Tugas: ${todos.size}",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // LAZY COLUMN DENGAN ANIMASI
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = todos,
                    key = { it.id }
                ) { todo ->
                    // ANIMASI MASUK UNTUK SETIAP ITEM
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        TodoItem(
                            todo = todo,
                            userData = userData,
                            viewModel = viewModel,
                            onNavigateToEdit = onNavigateToEdit,
                            modifier = Modifier.animateContentSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    userData: UserData?,
    viewModel: TodoViewModel,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Konversi string priority ke enum Priority
    val priority = try {
        Priority.valueOf(todo.priority)
    } catch (e: IllegalArgumentException) {
        Priority.MEDIUM // Default jika ada error
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNavigateToEdit(todo.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (priority) {
                Priority.LOW -> MaterialTheme.colorScheme.surfaceVariant
                Priority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // CHECKBOX DAN TITLE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(
                    checked = todo.isComplited,
                    onCheckedChange = { _ ->
                        userData?.userId?.let { userId ->
                            viewModel.toggle(userId, todo)
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = when (priority) {
                            Priority.LOW -> Color(0xFF4CAF50)
                            Priority.MEDIUM -> MaterialTheme.colorScheme.primary
                            Priority.HIGH -> MaterialTheme.colorScheme.error
                        }
                    )
                )

                Column {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (todo.isComplited) FontWeight.Normal else FontWeight.Medium
                        ),
                        color = if (todo.isComplited) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )

                    // PRIORITY BADGE
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (priority) {
                                    Priority.LOW -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    Priority.MEDIUM -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                    Priority.HIGH -> Color(0xFFF44336).copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = todo.priority,
                            fontSize = 10.sp,
                            color = when (priority) {
                                Priority.LOW -> Color(0xFF4CAF50)
                                Priority.MEDIUM -> Color(0xFF2196F3)
                                Priority.HIGH -> Color(0xFFF44336)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // DELETE BUTTON DENGAN ANIMASI
            IconButton(
                onClick = {
                    userData?.userId?.let { userId ->
                        viewModel.delete(userId, todo.id)
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}