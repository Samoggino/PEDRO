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
import androidx.compose.runtime.derivedStateOf
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.lam.pedro.R
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
import com.lam.pedro.presentation.screen.community.CommunityUserDetailsScreen
import com.lam.pedro.presentation.screen.community.chat.ChatScreen
import com.lam.pedro.presentation.screen.more.AboutScreen
import com.lam.pedro.presentation.screen.more.HealthConnectScreen
import com.lam.pedro.presentation.screen.more.PrivacyPolicyScreen
import com.lam.pedro.presentation.screen.more.SettingsScreen
import com.lam.pedro.presentation.screen.more.loginscreen.LoginScreen
import com.lam.pedro.presentation.screen.more.loginscreen.RegisterScreen
import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.presentation.screen.profile.ProfileScreen
import com.lam.pedro.presentation.serialization.MyScreenRecords
import com.lam.pedro.util.showExceptionSnackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Provides the navigation in the app. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PedroNavigation(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val titleId by remember(currentRoute) {
        derivedStateOf {
            when (currentRoute) {
                Screen.HomeScreen.route -> Screen.HomeScreen.titleId
                Screen.ActivitiesScreen.route -> Screen.ActivitiesScreen.titleId
                Screen.CommunityScreen.route -> Screen.CommunityScreen.titleId
                Screen.MoreScreen.route -> Screen.MoreScreen.titleId

                Screen.SleepSessions.route -> Screen.SleepSessions.titleId
                Screen.DriveSessionScreen.route -> Screen.DriveSessionScreen.titleId
                Screen.SitSessionScreen.route -> Screen.SitSessionScreen.titleId
                Screen.WeightScreen.route -> Screen.WeightScreen.titleId
                Screen.ListenSessionScreen.route -> Screen.ListenSessionScreen.titleId

                Screen.RunSessionScreen.route -> Screen.RunSessionScreen.titleId
                Screen.WalkSessionScreen.route -> Screen.WalkSessionScreen.titleId
                Screen.YogaSessionScreen.route -> Screen.YogaSessionScreen.titleId
                Screen.CycleSessionScreen.route -> Screen.CycleSessionScreen.titleId
                Screen.TrainSessionScreen.route -> Screen.TrainSessionScreen.titleId

                Screen.HealthConnectScreen.route -> Screen.HealthConnectScreen.titleId
                Screen.SettingScreen.route -> Screen.SettingScreen.titleId
                Screen.PrivacyPolicy.route -> Screen.PrivacyPolicy.titleId
                Screen.AboutScreen.route -> Screen.AboutScreen.titleId

                else -> R.string.app_name
            }
        }
    }

    fun onNavBack() {
        navController.popBackStack()
    }

    Log.d("Navigation", "PedroNavigation")

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

//     Stack per tenere traccia delle schermate aperte

    var sharedViewModel: ActivitySessionViewModel? by remember { mutableStateOf(null) }
    val sharedColor: Color = Color.Red
    var sharedTitle: Int? = 0

    //     Funzione per loggare le schermate attive
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
                Screen.CommunityScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                Log.d("Navigation", "CommunityScreen navigation")
                CommunityScreen(
                    onNavigateToChat = { userId ->
                        navController.navigate(Screen.ChatScreen.route + "/$userId")
                    },
                    onNavigateToUserDetails = { userId ->
                        navController.navigate(Screen.CommunityUserDetails.route + "/$userId")
                    },
                    onNavBack = { onNavBack() },
                    onLoginClick = { navController.navigate(Screen.LoginScreen.route) }
                )

            }

            composable(
                route = Screen.HomeScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                Log.d("Navigation", "HomeScreen navigation")
                HomeScreen(onProfileClick = { navController.navigate(Screen.ProfileScreen.route) })
            }
            composable(
                route = Screen.ActivitiesScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                ActivitiesScreen(navController)
            }

            composable(
                route = Screen.MoreScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                MoreScreen(navController)
            }
            composable(
                route = Screen.ProfileScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                logScreenStack()
                ProfileScreen(navController, titleId)
            }
            composable(
                route = Screen.AboutScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH,
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                AboutScreen(navController = navController, titleId = titleId)
            }
            composable(
                route = Screen.LoginScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                LoginScreen(navController)
            }

            composable(
                route = Screen.RegisterScreen.route,
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
                PrivacyPolicyScreen(navController = navController, titleId = titleId)
            }
            composable(
                route = Screen.HealthConnectScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                HealthConnectLauncher(availability, navController, titleId, scope)
            }
            composable(
                route = Screen.SettingScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SettingsScreen(navController = navController, titleId = titleId)
            }


            composable(
                route = Screen.NewActivityScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                sharedViewModel?.let { it1 ->
                    sharedColor.let { it2 ->
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    titleId = titleId,
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
                    CommunityUserDetailsScreen(
                        selectedUser = userId,
                        onNavBack = { onNavBack() })
                }
            }

            composable(
                route = Screen.ChatScreen.route + "/{currentUser}",
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) { backStackEntry ->
                val userJsonEncoded = backStackEntry.arguments?.getString("currentUser")
                val user = if (userJsonEncoded != null) {
                    User.fromEncodedString(userJsonEncoded)
                } else {
                    null
                }
                logScreenStack()
                Log.d("Navigation", "ChatScreen navigation")

                if (user != null) {
                    ChatScreen(selectedUser = user) { onNavBack() }
                }
            }

        }
    }
}

@Composable
private fun HealthConnectLauncher(
    availability: Int,
    navController: NavHostController,
    titleId: Int,
    scope: CoroutineScope
) {

    val context = LocalContext.current
    val healthConnectManager = remember { HealthConnectManager(context) }

    HealthConnectScreen(
        healthConnectAvailability = availability,
        onResumeAvailabilityCheck = { healthConnectManager.checkAvailability() },
        navController = navController,
        titleId = titleId,
        revokeAllPermissions = {
            scope.launch(Dispatchers.IO) { healthConnectManager.revokeAllPermissions() }
        }
    )
}
