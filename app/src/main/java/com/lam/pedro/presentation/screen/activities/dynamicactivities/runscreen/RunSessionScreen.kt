package com.lam.pedro.presentation.screen.activities.dynamicactivities.runscreen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.navigation.NavController
import com.lam.pedro.data.ExerciseSession
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.PermissionRequired
import com.lam.pedro.presentation.component.StartActivityComponent
import kotlinx.coroutines.delay
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunSessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    sessionsList: List<ExerciseSession>,
    uiState: RunSessionViewModel.UiState,
    onInsertClick: () -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    onStartRecording: () -> Unit = {},
    navController: NavController,
    titleId: Int,
    color: Color,
    viewModel: RunSessionViewModel
) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    var timerValue by remember { mutableStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var exerciseSessions by remember { mutableStateOf<List<ExerciseSessionRecord>>(emptyList()) }

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is RunSessionViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [SleepSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (uiState is RunSessionViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            // Resetta il timer
            timerValue = 0
            while (isRecording) {
                delay(1000) // Aspetta un secondo
                timerValue++
            }
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
        if (uiState != RunSessionViewModel.UiState.Uninitialized) {
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
                    // Interfaccia per avviare e fermare l'attività
                    item {
                        StartActivityComponent(color, viewModel)
                    }
                }
            }

        }

    }
}
