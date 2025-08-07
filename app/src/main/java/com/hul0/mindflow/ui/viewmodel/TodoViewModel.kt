// app/src/main/java/com/hul0/mindflow/ui/viewmodel/TodoViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.*
import com.hul0.mindflow.data.TodoRepository
import com.hul0.mindflow.model.TodoItem
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {
    val allTodos: LiveData<List<TodoItem>> = repository.allTodos.asLiveData()

    fun insert(todo: TodoItem) = viewModelScope.launch {
        repository.insert(todo)
    }

    fun update(todo: TodoItem) = viewModelScope.launch {
        repository.update(todo)
    }

    fun delete(todo: TodoItem) = viewModelScope.launch {
        repository.delete(todo)
    }
}



