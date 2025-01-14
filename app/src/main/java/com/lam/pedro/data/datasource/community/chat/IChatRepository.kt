package com.lam.pedro.data.datasource.community.chat

import com.lam.pedro.presentation.screen.community.chat.Message
import com.lam.pedro.presentation.screen.more.loginscreen.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface IChatRepository {
    val messages: StateFlow<List<Message>>
    suspend fun loadMessages(selectedUser: User, yourCoroutineScope: CoroutineScope)
    suspend fun sendMessage(conversation: List<Message>, message: String, sender: User)
}
