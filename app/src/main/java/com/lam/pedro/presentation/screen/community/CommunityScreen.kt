package com.lam.pedro.presentation.screen.community

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.lam.pedro.presentation.component.UserCard
import com.lam.pedro.presentation.component.UserPlaceholder
import com.lam.pedro.presentation.screen.loginscreen.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavHostController
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val viewModel: ViewModelFollowScreen = viewModel(factory = ViewModelFollowScreenFactory())
    val userFollowMap by viewModel.userFollowMap.collectAsState() // Osserva il Flow

    val coroutineScope = rememberCoroutineScope()

    // Carica i dati iniziali al primo caricamento
    LaunchedEffect(true) {
        Log.i("Supabase-Following", "FollowScreen")
        viewModel.getFollowedUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Follow Screen") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            // Schermata con PullToRefresh
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    coroutineScope.launch {
                        viewModel.getFollowedUsers()
                        isRefreshing = false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1B94F3))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row {
                        UserFollowList(
                            userFollowMap = userFollowMap,
                            onFollowToggle = { user, isFollowing ->
                                coroutineScope.launch {
                                    viewModel.toggleFollowUser(user, isFollowing)
                                    // Modifica solo lo stato senza ricaricare tutto
                                    userFollowMap?.let {
                                        val updatedMap = it.toMutableMap()
                                        updatedMap[user] = !isFollowing
                                        viewModel.updateFollowState(updatedMap)
                                    }
                                }
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FileUploadButton(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun UserFollowList(
    userFollowMap: Map<User, Boolean>?,
    onFollowToggle: (User, Boolean) -> Unit
) {
    val isLoading = userFollowMap == null
    val alpha by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f, // Se i dati sono in caricamento, mantieni la shimmer
        animationSpec = tween(
            durationMillis = 3000,
            easing = EaseInOut
        ), label = "FloatAnimation" // Animazione più lenta per il fade-out
    )

    LazyColumn(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLoading) {
            // Mostra skeleton loader durante il caricamento
            items(5) { UserPlaceholder(alpha = alpha) }
        } else {
            userFollowMap?.forEach { (user, isFollowing) ->
                item {
                    UserCard(
                        user = user,
                        isFollowing = isFollowing,
                        onClick = { onFollowToggle(user, isFollowing) }
                    )
                }
            }
        }
    }
}

@Composable
fun FileUploadButton(viewModel: ViewModelFollowScreen) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Launcher per aprire il file picker
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedFileUri ->
            coroutineScope.launch {
                Log.i("Supabase", "FileUploadButton")
                viewModel.uploadFileToSupabase(context, selectedFileUri)
            }
        }
    }

    // Bottone per attivare il file picker
    Button(
        onClick = {
            pickFileLauncher.launch("image/*") // Specifica il tipo di file da selezionare
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Carica avatar")
    }
}
