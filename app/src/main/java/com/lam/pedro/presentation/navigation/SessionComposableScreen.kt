package com.lam.pedro.presentation.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.GeneralActivityViewModelFactory
import com.lam.pedro.presentation.screen.activities.activitiyscreens.SessionScreen
import com.lam.pedro.util.showExceptionSnackbar
import kotlinx.coroutines.CoroutineScope


@Composable
fun SetupSessionScreen(
    screen: Screen,
    activityViewModel: ActivitySessionViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    topBarTitle: Int,
    //enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?,
    //exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?,
    screenStack: SnapshotStateList<String>,
    onSharedViewModelChange: (ActivitySessionViewModel) -> Unit,
    onSharedTitleChange: (Int) -> Unit
) {
    /*
    composable(
        screen.route,
        enterTransition = enterTransition,
        exitTransition = exitTransition
    ) {

     */


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
        screenStack.add(screen.route)
        Log.d("PedroNavigation", "Current screen stack: $screenStack")

        SessionScreen(
            permissions = permissions,
            permissionsGranted = permissionsGranted,
            uiState = activityViewModel.uiState,
            onError = { exception ->
                showExceptionSnackbar(snackbarHostState, scope, exception)
            },
            onPermissionsResult = onPermissionsResult,
            onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
            navController = navController,
            titleId = topBarTitle,
            viewModel = activityViewModel
        )
    //}
}