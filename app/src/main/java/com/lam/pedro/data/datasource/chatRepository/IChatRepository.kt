package com.lam.pedro.data.datasource.chatRepository

import com.lam.pedro.presentation.screen.community.chat.Message
import com.lam.pedro.presentation.screen.more.loginscreen.User
import kotlinx.coroutines.flow.StateFlow

interface IChatRepository {
    val messages: StateFlow<List<Message>>
    suspend fun loadMessages(selectedUser: User)
    suspend fun sendMessage(conversation: List<Message>, newMessageText: String, selectedUser: User)
}