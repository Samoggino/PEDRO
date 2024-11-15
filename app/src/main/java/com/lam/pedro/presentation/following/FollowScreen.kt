package com.lam.pedro.presentation.following

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.presentation.screen.loginscreen.User
import kotlinx.coroutines.launch

@Composable
fun FollowScreen() {
    // Simulazione di dati utente

//    var sleepSessions by remember { mutableStateOf(emptyList<SleepSessionData>()) }

    val context = LocalContext.current
    val viewModel: ViewModelFollowScreen = viewModel(factory = ViewModelFollowScreenFactory())
    var userFollowMap by remember { mutableStateOf(emptyMap<User, Boolean>()) }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(true) {
        // Esegui la chiamata al ViewModel per ottenere gli utenti seguiti
        userFollowMap = viewModel.getFollowedUsers(context)
    }

    UserFollowList(
        userFollowMap = userFollowMap,
        onFollowToggle = { user, isFollowing ->
            coroutineScope.launch {
                // Passa lo stato attuale di follow
                viewModel.toggleFollowUser(context, user, isFollowing)
                // Aggiorna la mappa degli utenti seguiti
                userFollowMap = viewModel.getFollowedUsers(context)
            }
        }

    )
}

@Composable
fun UserFollowList(userFollowMap: Map<User, Boolean>, onFollowToggle: (User, Boolean) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        userFollowMap.forEach { (user, isFollowing) ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFollowToggle(user, isFollowing) }, // Azione al click
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isFollowing) "Following" else "Not Following",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isFollowing) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                        Icon(
                            imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = if (isFollowing) "Followed" else "Follow",
                            tint = if (isFollowing) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
