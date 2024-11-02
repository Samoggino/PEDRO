package com.lam.pedro.presentation.screen.activities.dynamicactivities.yogascreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.PermissionRequired
import com.lam.pedro.presentation.component.StartActivityComponent
import com.lam.pedro.presentation.screen.activities.dynamicactivities.runscreen.RunSessionViewModel
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YogaSessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    //sessionsList: List<WalkSessionData>,
    uiState: YogaSessionViewModel.UiState,
    onInsertClick: () -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    onStartRecording: () -> Unit = {},
    navController: NavController,
    titleId: Int,
    color: Color,
    viewModel: YogaSessionViewModel
) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    // Per memorizzare lo stato del bottone e il tempo di inizio della sessione
    val isRecording = rememberSaveable { mutableStateOf(false) }
    val startTime = rememberSaveable { mutableStateOf<Instant?>(null) }

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is YogaSessionViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [SleepSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (uiState is YogaSessionViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = stringResource(titleId),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                navigationIcon = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        BackButton(navController)
                    }
                }
            )
        }

    ) { paddingValues ->
        if (uiState != YogaSessionViewModel.UiState.Uninitialized) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
//TODO: implementare la registrazione e la visualizzazione delle sessioni
                    item {
                        val healthConnectManager = viewModel.healthConnectManager
                        val runViewModel = RunSessionViewModel(healthConnectManager)
                        StartActivityComponent(color, runViewModel)
                    }
                }
            }
        }

    }

}