package com.learn.tugasuas.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// State untuk menyimpan data games, status loading, dan filter aktif
data class HomeState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "All"
)

class HomeViewModel : ViewModel() {
    private val repository = GameRepository()

    // State utama yang diobservasi oleh UI
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    // Cache untuk menyimpan SEMUA data game dari database sebelum difilter
    private val _allGames = MutableStateFlow<List<Game>>(emptyList())

    // Variable untuk menyimpan game yang sedang diedit
    var gameToEdit by mutableStateOf<Game?>(null)
        private set

    init {
        // Logic Reactive: Menggabungkan data asli + search query + category
        // Setiap kali salah satu berubah, list 'games' di _state akan otomatis terupdate
        viewModelScope.launch {
            combine(_allGames, _state) { games, state ->
                val query = state.searchQuery
                val category = state.selectedCategory

                // Proses filtering
                games.filter { game ->
                    val matchesQuery = game.title.contains(query, ignoreCase = true)
                    val matchesCategory = category == "All" || game.genre == category
                    matchesQuery && matchesCategory
                }
            }.collect { filteredGames ->
                // Update hanya list games yang tampil, tanpa mereset query/kategori
                if (_state.value.games != filteredGames) {
                    _state.value = _state.value.copy(games = filteredGames)
                }
            }
        }
    }

    // --- FUNGSI LOAD DATA ---
    fun loadGames(userId: String) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                repository.getGames(userId).collect { gameList ->
                    _allGames.value = gameList // Simpan ke cache _allGames
                    _state.value = _state.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    // --- FUNGSI CRUD (YANG SEBELUMNYA HILANG) ---

    fun addGame(game: Game) {
        viewModelScope.launch {
            try {
                repository.addGame(game)
                // Tidak perlu reload manual karena loadGames meng-observe Flow dari Repository
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal tambah: ${e.message}")
            }
        }
    }

    fun updateGame(game: Game) {
        viewModelScope.launch {
            try {
                repository.updateGame(game)
                // Reset edit mode setelah update selesai (opsional)
                gameToEdit = null
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal update: ${e.message}")
            }
        }
    }

    fun deleteGame(gameId: String) {
        viewModelScope.launch {
            try { repository.deleteGame(gameId) } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal hapus: ${e.message}")
            }
        }
    }

    // --- FUNGSI INTERAKSI UI ---

    fun onSearchQueryChange(newQuery: String) {
        _state.value = _state.value.copy(searchQuery = newQuery)
    }

    fun onCategorySelected(category: String) {
        _state.value = _state.value.copy(selectedCategory = category)
    }

    fun onAddClick() {
        gameToEdit = null
    }

    fun onEditClick(game: Game) {
        gameToEdit = game
    }
}