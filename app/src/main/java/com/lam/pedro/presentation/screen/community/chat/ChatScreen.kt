package com.lam.pedro.presentation.screen.community.chat

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.datasource.chatRepository.ChatRepositoryImpl
import com.lam.pedro.presentation.screen.more.loginscreen.User

@Composable
fun ChatScreen(selectedUser: User, onNavBack: () -> Unit) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ChatTopBar(selectedUser, onNavBack) },
        content = { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                MessagesList(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    selectedUser = selectedUser,
                )
                MessageInput(user = selectedUser)
            }
        }
    )
}

@Composable
fun MessageInput(user: User) {
    // Otteniamo il ViewModel
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(
            user = user,
            chatRepository = ChatRepositoryImpl()
        )
    )

    // Stato locale per il messaggio corrente
    var textState by remember { mutableStateOf("") }

    Log.i("ChatScreen", "MessageInput: $textState")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = textState,
            onValueChange = { newValue ->
                // Aggiornamento dello stato del campo di testo quando l'utente digita
                textState = newValue
            },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Scrivi un messaggio...") }
        )
        IconButton(
            onClick = {
                if (textState.isNotBlank()) {
                    // Passo il messaggio al ViewModel solo quando l'utente invia il messaggio
                    chatViewModel.sendMessage(newMessage = textState, selectedUser = user)
                    textState = "" // Resetta il campo di testo
                }
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Invia")
        }
    }
}

