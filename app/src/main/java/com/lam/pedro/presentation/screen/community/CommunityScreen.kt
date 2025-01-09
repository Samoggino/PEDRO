package com.lam.pedro.presentation.screen.community

import android.util.Log
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.presentation.component.UserCommunityCard
import com.lam.pedro.presentation.component.UserPlaceholder
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.util.vibrateOnClick
import com.lam.pedro.util.vibrateOnLongPress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val AnimationDuration = 2500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavController,
    viewModel: CommunityScreenViewModel = viewModel(factory = CommunityScreenViewModelFactory())
) {
    val coroutineScope = rememberCoroutineScope()

    // Usa remember per gli stati
    val isRefreshingState = remember { mutableStateOf(false) }
    val isInitialLoadState = remember { mutableStateOf(false) }
    val followingOnlyState = remember { mutableStateOf(false) }

    val userFollowMap by viewModel.userFollowMap.collectAsState(initial = emptyMap()) // Fornisci un valore iniziale
    val userIsLogged by viewModel.userIsLoggedIn.collectAsState(initial = false)


    Log.i("Community", "CommunityScreen")
    LaunchedEffect(isInitialLoadState.value) {
        if (!isInitialLoadState.value) {
            isRefreshingState.value = true
            if (userIsLogged) {
                viewModel.getFollowedUsers()
            }
            viewModel.updateUserIsLoggedIn()
            isRefreshingState.value = false
            isInitialLoadState.value = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (userIsLogged) {
                        IconButton(
                            onClick = {
                                followingOnlyState.value = !followingOnlyState.value
                                vibrateOnClick()
                            }
                        ) {
                            Icon(
                                imageVector = if (followingOnlyState.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Schermata con PullToRefresh
            PullToRefreshBox(
                isRefreshing = isRefreshingState.value,
                onRefresh = {
                    isRefreshingState.value = true
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.getFollowedUsers()
                        isRefreshingState.value = false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
            ) {

                if (userIsLogged) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Row {
                            UserFollowList(
                                userFollowMap = userFollowMap,
                                followingOnly = followingOnlyState.value, // Passa lo stato del filtro
                                onFollowToggle = { user, isFollowing ->
                                    coroutineScope.launch {
                                        viewModel.toggleFollowUser(user, isFollowing)
                                        userFollowMap?.let {
                                            val updatedMap = it.toMutableMap()
                                            updatedMap[user] = !isFollowing
                                            viewModel.updateFollowState(updatedMap)
                                        }
                                    }
                                },
                                isRefreshing = isRefreshingState.value,
                                isInitialLoad = isInitialLoadState.value,
                                navController = navController
                            )
                        }

//                        Row(
//                            modifier = Modifier.fillMaxSize(),
//                            horizontalArrangement = Arrangement.Center
//                        ) {
//                            FileUploadButton(viewModel)
//                        }
                    }
                } else {

                    // Mostra un messaggio se l'utente non è loggato
                    PlaceholderCommunity(navController)
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
    navController: NavController
) {
    var isNavigating by remember { mutableStateOf(false) } // Aggiungi questa variabile
    val animation by animateFloatAsState(
        targetValue = if (isRefreshing) 1f else 0f, // Se i dati sono in caricamento, mantieni la shimmer
        animationSpec = tween(
            durationMillis = 1500,
            easing = EaseInOut
        ), label = "FloatAnimation"
    )

    LazyColumn(modifier = Modifier.padding(4.dp)) {
        val userModifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

        if (isRefreshing && !isInitialLoad) {
            items(5) {
                UserPlaceholder(modifier = userModifier, animation = animation)
            }
        } else {

            val filteredUsers = if (followingOnly) {
                userFollowMap?.filter { it.value }?.toMap()
            } else {
                userFollowMap
            }

            filteredUsers?.forEach { (user, isFollowing) ->
                item {
                    UserCommunityCard(
                        user = user,
                        isFollowing = isFollowing,
                        onClick = {
                            vibrateOnClick()
                            onFollowToggle(user, isFollowing)
                        },
                        onLongPress = {
                            // Impedisci la navigazione ripetuta
                            if (!isNavigating) {
                                isNavigating = true
                                vibrateOnLongPress()
                                navController.navigate(Screen.CommunityUserDetails.route + "/${user.id}")
                                // Reset isNavigating dopo la navigazione
                                // Questo può essere fatto tramite un callback in LaunchedEffect se necessario
                                LaunchedEffect(Unit) {
                                    delay(300)
                                    // Una volta che la navigazione è completata, ripristina isNavigating
                                    isNavigating = false
                                }
                            }
                        },
                        modifier = userModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderCommunity(navController: NavController) {
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
                onClick = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Login")
            }
        }

    }
}
