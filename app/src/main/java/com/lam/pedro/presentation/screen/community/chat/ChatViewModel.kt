package com.lam.pedro.presentation.screen.community.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.chatRepository.IChatRepository
import com.lam.pedro.presentation.screen.more.loginscreen.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: IChatRepository, selectedUser: User) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages


    init {
        startPollingMessages(selectedUser)
    }

    private fun startPollingMessages(selectedUser: User) {
        viewModelScope.launch(Dispatchers.IO) {
            while (currentCoroutineContext().isActive) {
                Log.d("ChatViewModel", "Polling messages")
                try {
                    loadMessages(selectedUser)
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error during polling: ${e.message}")
                }
                delay(5000)
            }
        }
    }


    private fun loadMessages(selectedUser: User) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                chatRepository.loadMessages(selectedUser)
                // Dopo che i messaggi sono stati caricati inizialmente, aggiorniamo la UI
                _messages.value = chatRepository.messages.value
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading messages: ${e.message}")

            }
        }
    }

    fun sendMessage(newMessage: String, selectedUser: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                chatRepository.sendMessage(_messages.value, newMessage, selectedUser)
                _messages.value = chatRepository.messages.value
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message: ${e.message}")

            }
        }
    }

}

class ChatViewModelFactory(
    private val user: User,
    private val chatRepository: IChatRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatRepository, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}