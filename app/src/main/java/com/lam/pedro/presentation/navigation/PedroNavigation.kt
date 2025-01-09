package com.lam.pedro.presentation.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.datasource.SecurePreferencesManager.getMyContext
import com.lam.pedro.presentation.charts.ScreenCharts
import com.lam.pedro.presentation.screen.ActivitiesScreen
import com.lam.pedro.presentation.screen.HomeScreen
import com.lam.pedro.presentation.screen.MoreScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.GeneralActivityViewModelFactory
import com.lam.pedro.presentation.screen.activities.activitiyscreens.SessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.CycleSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.RunSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.TrainSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.WalkSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.YogaSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.DriveSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.LiftSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.ListenSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.SitSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.SleepSessionViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.NewActivityScreen
import com.lam.pedro.presentation.screen.community.CommunityScreen
import com.lam.pedro.presentation.screen.community.CommunityUserDetails
import com.lam.pedro.presentation.screen.more.AboutScreen
import com.lam.pedro.presentation.screen.more.HealthConnectScreen
import com.lam.pedro.presentation.screen.more.PrivacyPolicyScreen
import com.lam.pedro.presentation.screen.more.SettingsScreen
import com.lam.pedro.presentation.screen.more.loginscreen.LoginScreen
import com.lam.pedro.presentation.screen.more.loginscreen.RegisterScreen
import com.lam.pedro.presentation.screen.profile.ProfileScreen
import com.lam.pedro.presentation.serialization.MyScreenRecords
import com.lam.pedro.util.showExceptionSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Provides the navigation in the app. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PedroNavigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    topBarTitle: Int,
) {

    Log.d("PedroNavigation", "PedroNavigation")

    val fadeInTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
        {
            fadeIn(
                animationSpec = tween(700) // Personalizza la durata dell'animazione
            )
        }
    val fadeOutTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
        {
            fadeOut(
                animationSpec = tween(600)
            ) // Aggiungi un'animazione di uscita, se desiderato
        }
    val slideInH: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth }, // Entra da destra
            animationSpec = tween(700) // Durata dell'animazione
        )
    }
    val slideOutH: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth }, // Esce verso destra
            animationSpec = tween(600) // Durata dell'uscita
        )
    }
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
        {
            fadeIn(
                animationSpec =
                tween(1000) // Personalizza la durata dell'animazione
            )
        }
    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
        {
            fadeOut(
                animationSpec = tween(1000)
            ) // Aggiungi un'animazione di uscita, se desiderato
        }

    val scope = rememberCoroutineScope()

    // Stack per tenere traccia delle schermate aperte

    var sharedViewModel: ActivitySessionViewModel? by remember { mutableStateOf(null) }
    val sharedColor: Color? by remember { mutableStateOf(null) }
    var sharedTitle: Int? by remember { mutableStateOf(null) }

    // Funzione per loggare le schermate attive
    fun logScreenStack() {
        Log.d("Reload", "Ho cambiato pagina")
    }


    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {

            val healthConnectManager = HealthConnectManager(getMyContext())
            val availability by healthConnectManager.availability

            composable(
                Screen.HomeScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                HomeScreen(navController)
            }
            composable(
                Screen.ActivitiesScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                ActivitiesScreen(navController)
            }
            composable(
                Screen.CommunityScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                CommunityScreen(navController = navController)
            }
            composable(
                Screen.MoreScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                MoreScreen(navController)
            }
            composable(
                Screen.ProfileScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack()
                ProfileScreen(navController, topBarTitle)
            }
            composable(
                Screen.AboutScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH,
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                AboutScreen(navController = navController, titleId = topBarTitle)
            }
            composable(
                Screen.LoginScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                LoginScreen(navController)
            }

            composable(
                Screen.RegisterScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                RegisterScreen(navController)
            }


            composable(
                route = Screen.PrivacyPolicy.route,
                enterTransition = slideInH,
                exitTransition = slideOutH,
                deepLinks =
                listOf(
                    navDeepLink {
                        action = "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"
                    }
                )
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                PrivacyPolicyScreen(navController = navController, titleId = topBarTitle)
            }
            composable(
                Screen.HealthConnectScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                HealthConnectLauncher(availability, navController, topBarTitle, scope)
            }
            composable(
                Screen.SettingScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SettingsScreen(navController = navController, titleId = topBarTitle)
            }


            composable(
                Screen.NewActivityScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                sharedViewModel?.let { it1 ->
                    sharedColor?.let { it2 ->
                        sharedTitle?.let { it3 ->
                            NewActivityScreen(
                                navController = navController,
                                titleId = it3,
                                color = it2,
                                viewModel = it1
                            )
                        }
                    }
                }
            }

            composable(
                Screen.RunSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: RunSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.RunSessionScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }

                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }

                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(
                Screen.SleepSessions.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: SleepSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.SleepSessions.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(
                Screen.WalkSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: WalkSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.WalkSessionScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(
                Screen.DriveSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: DriveSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.DriveSessionScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(
                Screen.SitSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: SitSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.SitSessionScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(
                Screen.ListenSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: ListenSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.ListenSessionScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(Screen.WeightScreen.route) {
                val viewModel: LiftSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.WeightScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
                val permissions = viewModel.permissions
//                val sessionsList by viewModel.sessionsList

                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(
                Screen.YogaSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: YogaSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.YogaSessionScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(
                Screen.CycleSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: CycleSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.CycleSessionScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }

            composable(
                Screen.TrainSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val viewModel: TrainSessionViewModel =
                    viewModel(factory = GeneralActivityViewModelFactory())

                sharedViewModel = viewModel
                sharedTitle = Screen.TrainSessionScreen.titleId

                val permissionsGranted by viewModel.permissionsGranted
//                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = {
                    viewModel.initialLoad()
                }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values -> permissionsLauncher.launch(values) },
                    navController = navController,
                    titleId = topBarTitle,
                    viewModel = viewModel
                )
            }
            composable(Screen.MyScreenRecords.route) { MyScreenRecords(navController) }

            composable(
                route = Screen.ChartsScreen.route + "/{activityType}",
                arguments = listOf(navArgument("activityType") { type = NavType.StringType })
            ) { backStackEntry ->

                ScreenCharts(
                    activityEnum = ActivityEnum
                        .valueOf(backStackEntry.arguments?.getString("activityType") ?: ""),
                    navController = navController
                )
            }

            composable(
                route = Screen.CommunityUserDetails.route + "/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                if (userId != null) {
                    CommunityUserDetails(userUUID = userId, navController = navController)
                }
            }
        }
    }
}

@Composable
private fun HealthConnectLauncher(
    availability: Int,
    navController: NavHostController,
    topBarTitle: Int,
    scope: CoroutineScope
) {

    val context = LocalContext.current
    val healthConnectManager = remember { HealthConnectManager(context) }

    HealthConnectScreen(
        healthConnectAvailability = availability,
        onResumeAvailabilityCheck = { healthConnectManager.checkAvailability() },
        navController = navController,
        titleId = topBarTitle,
        revokeAllPermissions = {
            scope.launch(Dispatchers.IO) { healthConnectManager.revokeAllPermissions() }
        }
    )
}
