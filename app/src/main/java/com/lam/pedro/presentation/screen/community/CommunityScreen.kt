package com.lam.pedro.presentation.screen.community

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.presentation.component.ShowSessionDetails
import com.lam.pedro.presentation.component.UserCommunityCard
import com.lam.pedro.presentation.component.UserPlaceholder
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.community.CommunityScreenViewModel.activityMap
import com.lam.pedro.presentation.screen.community.CommunityScreenViewModel.fetchActivityMap
import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.util.vibrateOnClick
import com.lam.pedro.util.vibrateOnLongPress
import kotlinx.coroutines.launch

val IconSize = 70.dp
val FollowButtonSize = IconSize * 0.45f
val NameHeight = 24.dp
const val AnimationDuration = 2500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavController,
    viewModel: CommunityScreenViewModel = CommunityScreenViewModel,
) {
    val userFollowMap by viewModel.userFollowMap.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val userIsLogged = viewModel.userIsLoggedIn.collectAsState()


    var isRefreshing by remember { mutableStateOf(false) }
    var followingOnly by remember { mutableStateOf(false) }  // Usa lo stesso stato per cuore e filtro
    var mounted by remember { mutableStateOf(false) }
    // Carica i dati iniziali al primo caricamento

    LaunchedEffect(Unit) {
        if (!mounted) {
            isRefreshing = true
            Log.i("Community", "CommunityScreen")
            if (userIsLogged.value) {
                viewModel.getFollowedUsers()
            }
            viewModel.updateUserIsLoggedIn()
            isRefreshing = false
            mounted = true // Imposta mounted solo alla prima esecuzione
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = "Community",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {


                    if (userIsLogged.value) {
                        IconButton(
                            onClick = {
                                followingOnly = !followingOnly  // Cambia lo stato del filtro
                                vibrateOnClick()
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Animazione per il cuore (riempito o vuoto)
                            val heartScale by animateFloatAsState(
                                targetValue = if (followingOnly) 1.2f else 1f, // Aumenta la dimensione del cuore quando è pieno
                                animationSpec = tween(durationMillis = 300),
                                label = "" // Tempo di animazione
                            )

                            Icon(
                                imageVector = if (followingOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Follow filter",
                                tint = Color.Red.copy(alpha = heartScale) // Tint rosso e con alpha che cambia in base all'animazione
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
                    .background(MaterialTheme.colorScheme.surface),
            ) {

                if (userIsLogged.value) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Row {
                            UserFollowList(
                                userFollowMap = userFollowMap,
                                followingOnly = followingOnly, // Passa lo stato del filtro
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
                                isRefreshing = isRefreshing,
                                mounted = mounted
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FileUploadButton(viewModel)
                        }
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
private fun PlaceholderCommunity(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
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

@Composable
fun UserFollowList(
    userFollowMap: Map<User, Boolean>?,
    followingOnly: Boolean, // Stato del filtro
    onFollowToggle: (User, Boolean) -> Unit,
    isRefreshing: Boolean,
    mounted: Boolean
) {
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

        if (isRefreshing && !mounted) {
            // Mostra skeleton loader durante il caricamento
            items(5) {
                UserPlaceholder(animation = animation, modifier = userModifier)
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
                            vibrateOnLongPress()
                            // Mostra un popup con la cronologia delle attività dell'utente
                            ActivityHistoryPopup(user.id)
                        },
                        modifier = userModifier,
                    )
                }
            }
        }
    }
}

@Composable
fun FileUploadButton(viewModel: CommunityScreenViewModel) {

    // Launcher per aprire il file picker
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedFileUri ->
            Log.i("Community", "FileUploadButton")
            viewModel.uploadFileToSupabase(selectedFileUri)
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

@Composable
fun ActivityHistoryPopup(
    userUUID: String
) {

    val activityMap by activityMap.collectAsState(emptyMap())
    var showDialog by remember { mutableStateOf(false) }
    var selectedActivityType: ActivityEnum? by remember { mutableStateOf(null) }

    LaunchedEffect(userUUID) {
        fetchActivityMap(userUUID)
    }

    Column {
        Text(text = "Activity History", modifier = Modifier.padding(16.dp))

        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .heightIn(max = 300.dp)
        ) {
            try {
                items(activityMap.keys.toList()) { activityType ->
                    Text(
                        text = activityType.name,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                selectedActivityType = activityType
                                showDialog = true
                            }
                    )
                }
            } catch (e: Exception) {
                Log.e("Community", "Error showing dialog", e)
            }
        }

        if (showDialog && selectedActivityType != null) {
            val recentActivities = activityMap[selectedActivityType]?.takeLast(5) ?: emptyList()
            ActivityDetailsDialog(
                activities = recentActivities,
                onDismiss = { showDialog = false },
                activityType = selectedActivityType!!

            )
        }
    }
}

@Composable
fun ActivityDetailsDialog(
    activities: List<GenericActivity>,
    activityType: ActivityEnum,
    onDismiss: () -> Unit,
) {

//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text(text = "Recent ${activityType.name} Activities") },
//        text = {
//            LazyColumn(
//                modifier = Modifier.padding(8.dp).heightIn(max = 300.dp)
//            ) {
//                try {
//                    items(activities) { activity ->
//                        Text(text = "Date: ${activity.basicActivity.startTime}, Duration: ${activity.basicActivity.durationInMinutes()}")
//                    }
//                } catch (e: Exception) {
//                    Log.e("Community", "Error showing dialog", e)
//                }
//            }
//        },
//        confirmButton = {
//            Button(onClick = onDismiss) {
//                Text("Close")
//            }
//        }
//    )

    Column(
        modifier = Modifier.heightIn(max = 300.dp)
    ) {
        activities.forEach { activity ->
            ShowSessionDetails(activity)
        }
    }


}