package com.learn.tugasuas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.learn.tugasuas.data.model.Game
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GameRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val gamesCollection = firestore.collection("games")

    // CREATE
    suspend fun addGame(game: Game) {
        val documentId = gamesCollection.document().id
        val newGame = game.copy(id = documentId)
        gamesCollection.document(documentId).set(newGame).await()
    }

    // READ (Realtime)
    fun getGames(userId: String): Flow<List<Game>> = callbackFlow {
        val query = gamesCollection
            .whereEqualTo("userId", userId)
            .orderBy("title", Query.Direction.ASCENDING)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val games = snapshot.toObjects(Game::class.java)
                trySend(games)
            }
        }
        awaitClose { subscription.remove() }
    }

    // --- TAMBAHKAN FUNGSI INI (UPDATE) ---
    suspend fun updateGame(game: Game) {
        // Mengupdate dokumen berdasarkan ID-nya dengan data baru
        gamesCollection.document(game.id).set(game).await()
    }
    // -------------------------------------

    // DELETE
    suspend fun deleteGame(gameId: String) {
        gamesCollection.document(gameId).delete().await()
    }
}