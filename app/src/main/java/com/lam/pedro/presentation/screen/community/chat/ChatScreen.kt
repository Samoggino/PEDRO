package com.lam.pedro.presentation.screen.community.chat

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.presentation.screen.more.loginscreen.User

@Composable
fun ChatScreen(
    selectedUser: User,
    chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(selectedUser)),
    onNavBack: () -> Unit
) {
    val conversation by chatViewModel.messages.collectAsState()
    var currentMessage by remember { mutableStateOf("") }

    val isLoading by chatViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        Log.i("ChatScreen", "LaunchedEffect")
    }

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
                        onMessageChange = { currentMessage = it },
                        onSendClick = {
                            if (currentMessage.isNotBlank()) {
                                chatViewModel.sendMessage(
                                    conversation,
                                    currentMessage,
                                    selectedUser
                                )
                                currentMessage = ""
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun MessagesList(messages: List<Message>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        state = listState
    ) {
        items(messages) { message ->
            MessageBubble(message = message)
        }
    }
}


@Composable
fun MessageBubble(message: Message) {
    val isCurrentUser = message.sender.id == getUUID()!!

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (!isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(message.message)
                    Row(
                        modifier = Modifier.width(
                            if (isCurrentUser) 90.dp else 90.dp
                        ),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            // il timestamp deve include solo l'ora e i minuti se il messaggio è stato inviato oggi
                            // altrimenti metti la data
                            text = message.formattedTimestamp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    currentMessage: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = currentMessage,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Scrivi un messaggio...") }
        )
        IconButton(
            onClick = onSendClick,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Invia")
        }

    }
}
