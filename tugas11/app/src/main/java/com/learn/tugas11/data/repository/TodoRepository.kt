package com.learn.tugas11.data.repository

import com.learn.tugas11.data.model.Priority
import com.learn.tugas11.data.model.Todo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TodoRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun getTodoCollection(userId: String) =
        firestore.collection("users").document(userId).collection("todos")

    fun getTodos(userId: String): Flow<List<Todo>> = callbackFlow {
        val subscription = getTodoCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val todos = snapshot.documents.mapNotNull {
                        it.toObject(Todo::class.java)?.copy(id = it.id)
                    }
                    trySend(todos)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addTodo(userId: String, title: String, priority: Priority = Priority.MEDIUM) {
        val todo = Todo(title = title, priority = priority.name)
        getTodoCollection(userId).add(todo).await()
    }

    suspend fun updateTodoStatus(userId: String, todoId: String, isCompleted: Boolean) {
        getTodoCollection(userId).document(todoId).update("isCompleted", isCompleted)
    }

    suspend fun updateTodoTitle(userId: String, todoId: String, newTitle: String) {
        getTodoCollection(userId).document(todoId).update("title", newTitle)
    }

    suspend fun updateTodoPriority(userId: String, todoId: String, priority: Priority) {
        getTodoCollection(userId).document(todoId).update("priority", priority.name)
    }

    suspend fun deleteTodo(userId: String, todoId: String) {
        getTodoCollection(userId).document(todoId).delete().await()
    }
}