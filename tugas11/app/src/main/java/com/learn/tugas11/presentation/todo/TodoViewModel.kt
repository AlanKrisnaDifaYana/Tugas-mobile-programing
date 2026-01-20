package com.learn.tugas11.presentation.todo

import com.learn.tugas11.data.model.Todo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.tugas11.data.model.Priority

import com.learn.tugas11.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos = _todos.asStateFlow()

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect { _todos.value = it }
        }
    }

    fun add(userId: String, title: String, selectedPriority: Priority) = viewModelScope.launch {
        repository.addTodo(userId, title)
    }

    fun toggle(userId: String, todo: Todo) = viewModelScope.launch {
        repository.updateTodoStatus(userId, todo.id, !todo.isComplited)
    }

    fun updateTitle(userId: String, todoId: String, newTitle: String) =
        viewModelScope.launch {
            repository.updateTodoTitle(userId, todoId, newTitle)
        }

    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        repository.deleteTodo(userId, todoId)
    }
}