package com.lam.pedro.presentation.screen.activities.staticactivities.listenscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.presentation.component.ActivityScreenHeader
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.PermissionRequired
import com.lam.pedro.presentation.component.SessionHistoryRow
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.activities.ActivitySessionViewModel
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListenSessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    sessionsList: List<ExerciseSession>,
    uiState: ActivitySessionViewModel.UiState,
    onInsertClick: () -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    onStartRecording: () -> Unit = {},
    navController: NavController,
    titleId: Int,
    color: Color,
    image: Int,
    viewModel: ListenSessionViewModel
) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    // Per memorizzare lo stato del bottone e il tempo di inizio della sessione
    val isRecording = rememberSaveable { mutableStateOf(false) }
    val startTime = rememberSaveable { mutableStateOf<Instant?>(null) }
    val sessionList by viewModel.sessionsList

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is ActivitySessionViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [SleepSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (uiState is ActivitySessionViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    Scaffold(
        floatingActionButton = {
            if (permissionsGranted) {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.NewActivityScreen.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add Activity") },
                    text =
                    { Text("Start Session") },
                    shape = RoundedCornerShape(26.dp),
                    containerColor = color, // Colore del bottone
                    contentColor = Color.White // Colore del contenuto (testo e icona)
                )
            }
        }
    ) { paddingValues ->
        if (uiState != ActivitySessionViewModel.UiState.Uninitialized) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    ActivityScreenHeader(titleId, color, image)
                }
                if (!permissionsGranted) {
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }

                    item {
                        PermissionRequired(
                            color = color,
                            permissions = permissions,
                            onPermissionLaunch = onPermissionsLaunch
                        )
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(26.dp))
                                .height(180.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            // TODO: graph
                        }
                        Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = stringResource(R.string.activity_history),
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier
                                .clip(RoundedCornerShape(26.dp))
                                .height(350.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            items(sessionList) { session ->
                                SessionHistoryRow(color, image, session, viewModel)
                                HorizontalDivider(
                                    thickness = 1.dp, // Spessore della linea
                                    color = Color(0xFF606060) // Colore della linea
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }
//TODO: implementare la registrazione e la visualizzazione delle sessioni
                }
            }
        }

    }

}