// alankrisnadifayana/tugas-mobile-programing/Tugas-mobile-programing-main/TugasUas/app/src/main/java/com/learn/tugasuas/data/repository/GameRepository.kt

package com.learn.tugasuas.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.learn.tugasuas.data.model.Game
import com.learn.tugasuas.data.model.Category
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GameRepository {
    private val firestore = FirebaseFirestore.getInstance()
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