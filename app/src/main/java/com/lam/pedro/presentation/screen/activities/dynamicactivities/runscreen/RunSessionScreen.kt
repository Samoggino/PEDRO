package com.lam.pedro.presentation.screen.activities.dynamicactivities.runscreen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.lam.pedro.presentation.screen.activities.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.SessionScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RunSessionScreen(
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
    viewModel: RunSessionViewModel
) {

    SessionScreen(
        permissions = permissions,
        permissionsGranted = permissionsGranted,
        uiState = uiState,
        onInsertClick = onInsertClick,
        onError = onError,
        onPermissionsResult = onPermissionsResult,
        onPermissionsLaunch = onPermissionsLaunch,
        onStartRecording = onStartRecording,
        navController = navController,
        titleId = titleId,
        color = color,
        image = image,
        viewModel = viewModel
    )

    /*
    val sessionList by viewModel.sessionsList
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    val listState = rememberLazyListState()

    LaunchedEffect(uiState) {
        if (uiState is ActivitySessionViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }
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
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
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
                    item { Spacer(modifier = Modifier.height(30.dp)) }
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
                            style = MaterialTheme.typography.headlineSmall
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
                                .height(600.dp)
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
