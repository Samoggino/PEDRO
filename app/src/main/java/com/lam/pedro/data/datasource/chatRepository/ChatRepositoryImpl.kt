package com.lam.pedro.data.datasource.chatRepository

import android.util.Log
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.screen.community.chat.Chat
import com.lam.pedro.presentation.screen.community.chat.Message
import com.lam.pedro.presentation.screen.more.loginscreen.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import java.time.ZoneId
import java.time.ZonedDateTime

class ChatRepositoryImpl : IChatRepository {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    override val messages: StateFlow<List<Message>> get() = _messages

    private val chatId = MutableStateFlow("")


    override suspend fun loadMessages(selectedUser: User) {
        // Simula una chiamata al server
        val newMessages = fetchMessagesFromServer(selectedUser)
        _messages.value = newMessages
    }

    private suspend fun fetchMessagesFromServer(
        selectedUser: User
    ): List<Message> {

        val currentUser = getUUID()

        val json = buildJsonObject {
            put("input_user1", currentUser)
            put("input_user2", selectedUser.id)
        }

        try {
            Log.d("ChatRepository", "ChatId: ${chatId.value}")

            val chat = withContext(Dispatchers.IO) {
                supabase().postgrest.rpc(
                    function = "get_or_create_chat",
                    parameters = json
                ).decodeSingle<Chat>()
            }

            chatId.value = chat.uuidCHAT
            return chat.conversation


        } catch (e: Exception) {
            Log.e("ChatRepository", "Error loading messages: ${e.message}")
        }
        return emptyList()
    }

    override suspend fun sendMessage(
        conversation: List<Message>,
        newMessageText: String,
        selectedUser: User
    ) {
        val updatedConversation = fetchMessagesFromServer(selectedUser) + Message(
            message = newMessageText,
            timestamp = ZonedDateTime.now(ZoneId.of("Europe/Rome")).toString(),
            sender = getUUID()!!
        )

        try {
            // Aggiorna la conversazione nel database
            val updatedChat = withContext(Dispatchers.IO) {
                supabase().from("chat")
                    .update({
                        set("conversation", Json.encodeToJsonElement(updatedConversation))
                    }) {
                        select()
                        filter { eq("uuidCHAT", chatId.value) }
                    }.decodeSingle<Chat>()
            }

            _messages.value = updatedChat.conversation

        } catch (e: Exception) {
            Log.e("ChatRepository", "Error sending message: ${e.message}")
        }
    }
}
