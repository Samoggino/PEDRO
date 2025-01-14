package com.lam.pedro.presentation.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.SessionScreen
import com.lam.pedro.util.showExceptionSnackbar
import kotlinx.coroutines.CoroutineScope


@Composable
fun SetupSessionScreen(
    screen: Screen,
    activityViewModel: ActivitySessionViewModel,
    onNavigate: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    topBarTitle: Int,
//    screenStack: SnapshotStateList<String>,
    onSharedViewModelChange: (ActivitySessionViewModel) -> Unit,
    onSharedTitleChange: (Int) -> Unit
) {

    // Aggiorna il ViewModel e il titolo condiviso
    onSharedViewModelChange(activityViewModel)
    onSharedTitleChange(screen.titleId)

    val permissionsGranted by activityViewModel.permissionsGranted
    val permissions = activityViewModel.permissions
    val onPermissionsResult = { activityViewModel.initialLoad() }
    val permissionsLauncher =
        rememberLauncherForActivityResult(activityViewModel.permissionsLauncher) {
            onPermissionsResult()
        }

    // Aggiorna lo stack delle schermate
//    screenStack.add(screen.route)
//    Log.d("PedroNavigation", "Current screen stack: $screenStack")

    SessionScreen(
        permissions = permissions,
        permissionsGranted = permissionsGranted,
        uiState = activityViewModel.uiState,
        onError = { exception -> showExceptionSnackbar(snackbarHostState, scope, exception) },
        onPermissionsResult = onPermissionsResult,
        onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
        onNavigate = onNavigate,
        titleId = topBarTitle,
        viewModel = activityViewModel
    )

}