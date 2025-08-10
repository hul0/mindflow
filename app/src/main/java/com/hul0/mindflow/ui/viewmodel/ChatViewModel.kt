// hul0/mindflow/mindflow-420a1f3c6faf5a0e40f158d1d0e60c100c99aee9/app/src/main/java/com/hul0/mindflow/ui/viewmodel/ChatViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.ChatRepository
import com.hul0.mindflow.model.ChatMessage
import com.hul0.mindflow.model.ChatRoom
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms.asStateFlow()

    private val _selectedRoomId = MutableStateFlow<Long?>(null)
    val selectedRoomId: StateFlow<Long?> = _selectedRoomId.asStateFlow()

    val messages: StateFlow<List<ChatMessage>> = _selectedRoomId.flatMapLatest { roomId ->
        if (roomId != null) {
            repository.getMessagesForRoom(roomId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    init {
        viewModelScope.launch {
            repository.getAllRooms().collect { rooms ->
                _chatRooms.value = rooms
                if (_selectedRoomId.value == null && rooms.isNotEmpty()) {
                    _selectedRoomId.value = rooms.first().id
                }
            }
        }
    }

    fun selectRoom(roomId: Long) {
        _selectedRoomId.value = roomId
    }

    fun createNewChat(chatName: String) {
        viewModelScope.launch {
            val newRoomId = repository.createNewRoom(chatName)
            _selectedRoomId.value = newRoomId
        }
    }

    fun sendMessage(message: String) {
        val roomId = _selectedRoomId.value ?: return
        if (message.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(roomId, message, true)
            _isLoading.value = true
            // Add your OpenRouter API Key here. It's recommended to store it.
            val apiKey = "sk-or-v1-6eb50b6b8113ecec1b2e8b96fa6d4ad4ea64cf3a202961e1cd942b45d914587"
            val history = messages.value
            val aiResponse = repository.getAIResponse(apiKey, history)
            if (aiResponse != null) {
                repository.sendMessage(roomId, aiResponse, false)
            }
            _isLoading.value = false
        }
    }
    fun deleteRoom(roomId: Long) {
        viewModelScope.launch {
            repository.deleteRoom(roomId)
            if (_selectedRoomId.value == roomId) {
                _selectedRoomId.value = _chatRooms.value.firstOrNull()?.id
            }
        }
    }
}
