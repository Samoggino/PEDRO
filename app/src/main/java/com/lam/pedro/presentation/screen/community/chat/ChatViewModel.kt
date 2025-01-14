package com.lam.pedro.presentation.screen.community.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.screen.more.loginscreen.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

class ChatViewModel(selectedUser: User) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    val isLoading = MutableStateFlow(false)

    private val chatId = MutableStateFlow("")


    init {
        loadMessages(selectedUser)
    }

    private fun loadMessages(selectedUser: User) {

        Log.i("ChatViewModel", "LoadMessage")

        val currentUser = getUUID()
        isLoading.value = true

        val json = buildJsonObject {
            put("input_user1", currentUser)
            put("input_user2", selectedUser.id)
        }

        try {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.i("ChatViewModel", "Pre rpc $json")

                    // Chiamata alla funzione RPC
                    val chat = supabase().postgrest.rpc(
                        function = "get_or_create_chat",
                        parameters = json
                    ).decodeSingle<Chat>()


                    // Verifica se la conversazione è vuota
                    if (chat.conversation.isEmpty()) {
                        Log.i("ChatViewModel", "No messages in the conversation")
                    }

                    // Aggiungi i messaggi
                    if (chat.conversation.isNotEmpty()) {
                        _messages.value = chat.conversation
                    } else {
                        Log.i("ChatViewModel", "No messages in the conversation")
                    }

                    // Imposta l'ID della chat
                    chatId.value = chat.uuidCHAT

                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error loading messages: ${e.message}")
                } finally {
                    // Imposta il flag isLoading a false
                    isLoading.value = false
                }
            }

        } catch (e: Exception) {
            print(Log.e("ChatViewModel", "Error loading messages: ${e.message}"))
            isLoading.value = false
        }
        isLoading.value = false
    }

    fun sendMessage(conversation: List<Message>, message: String, sender: User) {
        // Aggiungi il nuovo messaggio alla conversazione
        val updatedConversation = conversation + Message(
            message = message,
            timestamp = Clock.System.now().toString(),
            sender = sender
        )

        isLoading.value = true
        try {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Aggiorna la conversazione nel database passando direttamente conversation

                    val updatedChat = supabase().from("chat")
                        .update({
                            set(
                                "conversation",
                                Json.encodeToJsonElement(updatedConversation)
                            )
                        }
                        ) {
                            select()
                            filter { eq("uuidCHAT", chatId.value) }
                        }.decodeSingle<Chat>()

                    // Aggiorna la lista di messaggi
                    _messages.value = updatedChat.conversation


                    Log.i("ChatViewModel", "L'update è andato a buon fine")

                } catch (e: Exception) {
                    // Gestisci gli errori
                    Log.e("ChatViewModel", "Error sending message: ${e.message}")
                    println("Error creating or updating chat: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Error sending message: ${e.message}")
            isLoading.value = false
        }
        isLoading.value = false
    }
}

class ChatViewModelFactory(private val user: User) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



