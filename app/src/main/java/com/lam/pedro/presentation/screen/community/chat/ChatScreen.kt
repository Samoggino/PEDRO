package com.lam.pedro.presentation.screen.community.chat

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.datasource.community.chat.ChatRepositoryImpl
import com.lam.pedro.presentation.screen.more.loginscreen.User

@Composable
fun ChatScreen(
    selectedUser: User,
    chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(selectedUser, ChatRepositoryImpl())),
    onNavBack: () -> Unit
) {
    val conversation by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    // Memorizziamo il testo localmente, senza influire sulla composizione
    var currentMessage by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ChatTopBar(selectedUser, onNavBack) },
        content = { padding ->

            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    MessagesList(
                        messages = conversation,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                    MessageInput(
                        currentMessage = currentMessage,
                        onMessageChange = { newMessage ->
                            // Aggiorniamo il testo del messaggio solo a livello locale
                            currentMessage = newMessage
                        },
                        onSendClick = {
                            if (currentMessage.text.isNotBlank()) {
                                // Invia il messaggio e resetta il campo
                                chatViewModel.sendMessage(
                                    conversation,
                                    currentMessage.text,
                                    selectedUser
                                )
                                currentMessage =
                                    TextFieldValue("") // Resetta il messaggio dopo l'invio
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun MessageInput(
    currentMessage: TextFieldValue,
    onMessageChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit
) {
    // Gestione del valore del campo di testo senza scatenare una ricomposizione
    val textState = remember { mutableStateOf(currentMessage) }

    Log.i("ChatScreen", "MessageInput: ${currentMessage.text}")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = textState.value,
            onValueChange = { newValue ->
                // Solo aggiorniamo lo stato del campo di testo senza causare una ricomposizione
                textState.value = newValue
                onMessageChange(newValue) // Passiamo la nuova value al livello superiore
            },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Scrivi un messaggio...") }
        )
        IconButton(
            onClick =
            {
                onSendClick()
                textState.value = TextFieldValue("") // Resetta il campo di testo dopo l'invio
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Invia")
        }
    }
}
