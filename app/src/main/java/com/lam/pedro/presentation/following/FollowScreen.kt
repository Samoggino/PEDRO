package com.lam.pedro.presentation.following

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.lam.pedro.presentation.screen.loginscreen.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen() {
    var isRefreshing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: ViewModelFollowScreen = viewModel(factory = ViewModelFollowScreenFactory())
    var userFollowMap by remember { mutableStateOf<Map<User, Boolean>?>(null) } // Cambiato a nullable per rappresentare il caricamento
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        userFollowMap = viewModel.getFollowedUsers(context) // Carica i dati
    }

    when (userFollowMap) {
        null -> LoadingScreen() // Mostra un indicatore di caricamento
        else -> {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    coroutineScope.launch {
                        userFollowMap = viewModel.getFollowedUsers(context)
                        isRefreshing = false
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1B94F3)), // Colore di sfondo chiaro
            ) {
                UserFollowList(
                    userFollowMap = userFollowMap!!,
                    onFollowToggle = { user, isFollowing ->
                        Log.i("Supabase", "Toggling follow for user: $user")
                        coroutineScope.launch {
                            viewModel.toggleFollowUser(context, user, isFollowing)
                            userFollowMap = viewModel.getFollowedUsers(context)
                        }
                    }
                )
            }
            FileUploadButton(viewModel)

        }
    }
}

@Composable
fun FileUploadButton(viewModel: ViewModelFollowScreen) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Log.i("Supabase", "FileUploadButton")

    // Launcher per aprire il file picker
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedFileUri ->
            coroutineScope.launch {
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
        Text("Carica File")
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator() // Indicatore di caricamento
        Text(text = "Loading users...", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun UserFollowList(
    userFollowMap: Map<User, Boolean>?,
    onFollowToggle: (User, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (userFollowMap == null) {
            // Mostra placeholder
            Log.i("Supabase", "UserFollowMap is null")
            items(5) { // Mostra 5 placeholder
                UserPlaceholder()
            }
        } else {
            userFollowMap.forEach { (user, isFollowing) ->
                item {
                    UserCard(
                        user = user,
                        isFollowing = isFollowing,
                        onClick = { onFollowToggle(user, isFollowing) })
                }
            }
        }
    }
}

@Composable
fun UserPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simula un avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
            )
            // Simula le righe di testo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.5f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .fillMaxWidth(0.3f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}

@Composable
fun UserCard(user: User, isFollowing: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            // Stampa l'avatar, l'email e lo stato di follow
            Log.i("Supabase", "UserCard: $user isFollowing: $isFollowing")

            // Carica l'immagine dell'avatar o mostra un'icona di fallback
            if (user.avatarUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = user.avatarUrl)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
            } else {
                // Mostra un'icona di default se l'avatarUrl è nullo o vuoto
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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

@Preview(showBackground = true)
@Composable
fun FollowScreenPreview() {
    val dummyUserFollowMap = mapOf(
        User("1", "user1@example.com", "") to true,
        User("2", "user2@example.com","") to false,
        User("3", "user3@example.com","") to true
    )

    UserFollowList(
        userFollowMap = dummyUserFollowMap,
        onFollowToggle = { _, _ -> }
    )
}
