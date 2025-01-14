package com.lam.pedro.presentation.screen.community.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.community.chat.IChatRepository
import com.lam.pedro.presentation.screen.more.loginscreen.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: IChatRepository, selectedUser: User) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    val isLoading = MutableStateFlow(false)

    init {
        loadMessages(selectedUser)
    }

    private fun loadMessages(selectedUser: User) {
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                chatRepository.loadMessages(selectedUser, viewModelScope)
                // Dopo che i messaggi sono stati caricati inizialmente, aggiorniamo la UI
                _messages.value = chatRepository.messages.value
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading messages: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun sendMessage(conversation: List<Message>, message: String, sender: User) {
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                chatRepository.sendMessage(conversation, message, sender)
                _messages.value = chatRepository.messages.value
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
}


class ChatViewModelFactory(private val user: User, private val chatRepository: IChatRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRepository, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}