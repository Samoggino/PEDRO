package com.lam.pedro.presentation.screen.community

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.presentation.component.CommunityTopBar
import com.lam.pedro.presentation.component.UserCommunityCard
import com.lam.pedro.presentation.component.UserPlaceholder
import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.util.vibrateOnClick
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val AnimationDuration = 2500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToUserDetails: (String) -> Unit,
    onNavBack: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: CommunityScreenViewModel = viewModel(factory = CommunityScreenViewModelFactory())
) {
    Log.i("Community", "CommunityScreen reloaded")

    // Usa remember per gli stati
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val userIsLogged by viewModel.userIsLoggedIn.collectAsState()

    val followingOnlyState = remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            CommunityTopBar(
                userIsLogged = userIsLogged,
                followingOnlyState = followingOnlyState,
                onNavBack = onNavBack
            )
        },
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.refreshData()
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                val isInitialLoad by viewModel.isInitialLoad.collectAsState()
                val userFollowMap by viewModel.userFollowMap.collectAsState()

                if (userIsLogged) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        UserFollowList(
                            userFollowMap = userFollowMap,
                            followingOnly = followingOnlyState.value,
                            onFollowToggle = { user, isFollowing ->
                                viewModel.toggleFollowUser(user, isFollowing)
                            },
                            isRefreshing = isRefreshing,
                            isInitialLoad = isInitialLoad,
                            onNavigateToChat = onNavigateToChat,
                            onNavigateToUserDetails = onNavigateToUserDetails
                        )
                    }
                } else {
                    // Mostra un messaggio se l'utente non è loggato
                    NotInTheCommunity(onLoginClick)
                }
            }
        }
    }
}

@Composable
fun UserFollowList(
    userFollowMap: Map<User, Boolean>?,
    followingOnly: Boolean,
    onFollowToggle: (User, Boolean) -> Unit,
    isRefreshing: Boolean,
    isInitialLoad: Boolean,
    onNavigateToChat: (String) -> Unit,
    onNavigateToUserDetails: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val debounceJob = remember { mutableStateOf<Job?>(null) }

    fun debounceClick(action: () -> Unit) {
        debounceJob.value?.cancel()
        debounceJob.value = coroutineScope.launch {
            delay(600)
            action()
        }
    }

    val filteredUsers = userFollowMap?.let { map ->
        map.filter { it.value || !followingOnly }
    }

    LazyColumn(modifier = Modifier.padding(4.dp)) {
        if (isInitialLoad) {
            // Mostra 5 placeholders mentre i dati sono in fase di caricamento
            items(5) {
                UserPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    animation = animateFloatAsState(if (isRefreshing) 1f else 0f, label = "").value
                )
            }
        } else if (userFollowMap.isNullOrEmpty()) {
            // Mostra un messaggio se non ci sono utenti
            item {
                Text(
                    text = "No users found",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        } else {
            // Visualizza la lista di utenti
            filteredUsers?.forEach { (user, isFollowing) ->
                item(key = user.id) {
                    UserCommunityCard(
                        user = user,
                        isFollowing = isFollowing,
                        onFollowClick = {
                            vibrateOnClick()
                            onFollowToggle(user, isFollowing)
                        },
                        onChatClick = {
                            debounceClick {
                                onNavigateToChat(user.toEncodedString())  // Usa la funzione passata
                            }
                        },
                        onLongPress = {
                            debounceClick {
                                onNavigateToUserDetails(user.id)  // Usa la funzione passata
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun NotInTheCommunity(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row {
            Text(
                text = "You need to be logged in to access the community",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.padding(16.dp),
                content = { Text("Login") }
            )
        }
    }
}