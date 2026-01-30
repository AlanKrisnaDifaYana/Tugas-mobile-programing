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
import kotlinx.coroutines.launch

data class HomeState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel : ViewModel() {
    private val repository = GameRepository()

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    // Variabel untuk menyimpan game yang sedang diedit
    var gameToEdit by mutableStateOf<Game?>(null)
        private set

    fun loadGames(userId: String) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                repository.getGames(userId).collect { gameList ->
                    _state.value = _state.value.copy(games = gameList, isLoading = false, errorMessage = null)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun addGame(game: Game) {
        viewModelScope.launch {
            try { repository.addGame(game) } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Gagal tambah: ${e.message}")
            }
        }
    }

    fun updateGame(game: Game) {
        viewModelScope.launch {
            try { repository.updateGame(game) } catch (e: Exception) {
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

    // Dipanggil dari MainActivity saat tombol Edit diklik
    fun onEditClick(game: Game) {
        gameToEdit = game
    }

    // Dipanggil dari MainActivity saat tombol Tambah diklik
    fun onAddClick() {
        gameToEdit = null
    }
}