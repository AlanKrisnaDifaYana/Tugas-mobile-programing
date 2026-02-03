package com.learn.tugasuas.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.data.model.Category
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GameRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance() // Inisialisasi Storage
    private val gamesCollection = firestore.collection("games")
    private val categoriesCollection = firestore.collection("categories")

    // --- GAME CRUD ---
    suspend fun addGame(game: Game) {
        val documentId = gamesCollection.document().id
        gamesCollection.document(documentId).set(game.copy(id = documentId)).await()
    }

    fun getGames(userId: String): Flow<List<Game>> = callbackFlow {
        val query = gamesCollection.whereEqualTo("userId", userId).orderBy("title", Query.Direction.ASCENDING)
        val subscription = query.addSnapshotListener { snapshot, _ ->
            snapshot?.let { trySend(it.toObjects(Game::class.java)) }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun updateGame(game: Game) = gamesCollection.document(game.id).set(game).await()
    suspend fun deleteGame(gameId: String) = gamesCollection.document(gameId).delete().await()

    // --- FITUR BARU: UPLOAD IMAGE ---
    suspend fun uploadImageToStorage(imageUri: Uri): String {
        return try {
            // Membuat nama file unik berdasarkan waktu
            val filename = "game_images/${System.currentTimeMillis()}.jpg"
            val ref = storage.reference.child(filename)

            // Upload file
            ref.putFile(imageUri).await()

            // Ambil URL download setelah upload selesai
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "" // Return string kosong jika gagal
        }
    }

    // --- CATEGORY CRUD ---
    suspend fun addCategory(category: Category) {
        val id = categoriesCollection.document().id
        categoriesCollection.document(id).set(category.copy(id = id)).await()
    }

    fun getCategories(userId: String): Flow<List<Category>> = callbackFlow {
        val subscription = categoriesCollection.whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { trySend(it.toObjects(Category::class.java)) }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun deleteCategory(id: String) = categoriesCollection.document(id).delete().await()
}