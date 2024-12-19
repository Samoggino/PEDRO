package com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.sleepscreen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.SessionScreen

/**
 * Shows a week's worth of sleep data.
 */
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SleepSessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
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
    viewModel: SleepSessionViewModel
) {

    SessionScreen(
        permissions = permissions,
        permissionsGranted = permissionsGranted,
        uiState = uiState,
        onError = onError,
        onPermissionsResult = onPermissionsResult,
        onPermissionsLaunch = onPermissionsLaunch,
        navController = navController,
        titleId = titleId,
        color = color,
        image = image,
        viewModel = viewModel
    )

    /*
    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
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
        containerColor = Color.Transparent,
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
                    .padding(top = 0.dp),
                horizontalAlignment = if (!permissionsGranted) {
                    Alignment.CenterHorizontally
                } else {
                    Alignment.Start
                }
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

                    // Button per Start/Stop della registrazione
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
                }
            }
        }
    }

     */
}