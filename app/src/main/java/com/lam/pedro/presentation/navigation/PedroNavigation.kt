package com.lam.pedro.presentation.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.healthconnectsample.presentation.screen.HealthConnectScreen
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.presentation.charts.ScreenCharts
import com.lam.pedro.presentation.charts.viewModelChartsFactory
import com.lam.pedro.presentation.screen.ActivitiesScreen
import com.lam.pedro.presentation.screen.HomeScreen
import com.lam.pedro.presentation.screen.MoreScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.GeneralActivityViewModelFactory
import com.lam.pedro.presentation.screen.activities.activitiyscreens.SessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels.CycleSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels.RunSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels.TrainSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels.WalkSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels.YogaSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivitiesviewmodels.DriveSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivitiesviewmodels.LiftSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivitiesviewmodels.ListenSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivitiesviewmodels.SitSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivitiesviewmodels.SleepSessionViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.NewActivityScreen
import com.lam.pedro.presentation.screen.community.CommunityScreen
import com.lam.pedro.presentation.screen.loginscreen.LoginScreen
import com.lam.pedro.presentation.screen.more.AboutScreen
import com.lam.pedro.presentation.screen.more.PrivacyPolicyScreen
import com.lam.pedro.presentation.screen.more.SettingsScreen
import com.lam.pedro.presentation.screen.profile.ProfileScreen
import com.lam.pedro.presentation.serialization.MyScreenRecords
import com.lam.pedro.presentation.serialization.ViewModelRecordFactory
import com.lam.pedro.util.showExceptionSnackbar
import kotlinx.coroutines.launch

/** Provides the navigation in the app. */
@OptIn(ExperimentalAnimationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PedroNavigation(
    navController: NavHostController,
    healthConnectManager: HealthConnectManager,
    snackbarHostState: SnackbarHostState,
    topBarTitle: Int
) {

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
    val screenStack = remember { mutableStateListOf<String>() }

    var sharedViewModel: ActivitySessionViewModel? by remember { mutableStateOf(null) }
    var sharedTitle: Int? by remember { mutableStateOf(null) }

    // Funzione per loggare le schermate attive
    fun logScreenStack() {
        Log.d("PedroNavigation", "Current screen stack: $screenStack")
    }

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.HomeScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            val availability by healthConnectManager.availability
            composable(
                Screen.HomeScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                screenStack.add(Screen.HomeScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                HomeScreen(navController)
            }
            composable(
                Screen.ActivitiesScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                screenStack.add(Screen.ActivitiesScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                ActivitiesScreen(navController)
            }
            composable(
                Screen.CommunityScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                screenStack.add(Screen.CommunityScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                CommunityScreen(navController)
            }
            composable(
                Screen.MoreScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                screenStack.add(Screen.MoreScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                MoreScreen(navController, healthConnectManager)
            }
            composable(
                Screen.ProfileScreen.route,
                enterTransition = fadeInTransition,
                exitTransition = fadeOutTransition
            ) {
                screenStack.add(Screen.ProfileScreen.route)
                logScreenStack()
                ProfileScreen(navController, topBarTitle)
            }
            composable(
                Screen.AboutScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH,
            ) {
                screenStack.add(Screen.AboutScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                AboutScreen(navController = navController, titleId = topBarTitle)
            }
            composable(
                Screen.LoginScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                screenStack.add(Screen.LoginScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                LoginScreen(navController)
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
                screenStack.add(Screen.HealthConnectScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                PrivacyPolicyScreen(navController = navController, titleId = topBarTitle)
            }
            composable(
                Screen.HealthConnectScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                screenStack.add(Screen.HealthConnectScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                HealthConnectScreen(
                    healthConnectAvailability = availability,
                    onResumeAvailabilityCheck = { healthConnectManager.checkAvailability() },
                    navController = navController,
                    titleId = topBarTitle,
                    revokeAllPermissions = {
                        scope.launch { healthConnectManager.revokeAllPermissions() }
                    }
                )
            }
            composable(
                Screen.SettingScreen.route,
                enterTransition = slideInH,
                exitTransition = slideOutH
            ) {
                screenStack.add(Screen.SettingScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SettingsScreen(navController = navController, titleId = topBarTitle)
            }
            composable(
                Screen.NewActivityScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                screenStack.add(Screen.NewActivityScreen.route)
                logScreenStack()
                sharedViewModel?.let { it1 ->
                    sharedTitle?.let { it2 ->
                        NewActivityScreen(
                            navController = navController,
                            titleId = it2,
                            viewModel = it1
                        )
                    }

                }
            }

            composable(
                Screen.RunSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: RunSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.RunSessionScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }
            composable(
                Screen.SleepSessions.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: SleepSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.SleepSessions,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(
                Screen.WalkSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: WalkSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.WalkSessionScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(
                Screen.DriveSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: DriveSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.DriveSessionScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(
                Screen.SitSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: SitSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.SitSessionScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(
                Screen.ListenSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: ListenSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.ListenSessionScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(
                Screen.WeightScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: LiftSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.WeightScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(
                Screen.YogaSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: YogaSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.YogaSessionScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(
                Screen.CycleSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: CycleSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.CycleSessionScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(
                Screen.TrainSessionScreen.route,
                enterTransition = enterTransition,
                exitTransition = exitTransition
            ) {
                val activityViewModel: TrainSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                SetupSessionScreen(
                    screen = Screen.TrainSessionScreen,
                    activityViewModel = activityViewModel,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = topBarTitle,
                    //enterTransition = enterTransition,
                    //exitTransition = exitTransition,
                    screenStack = screenStack,
                    onSharedViewModelChange = { viewModel ->
                        sharedViewModel = viewModel
                    },
                    onSharedTitleChange = { titleId ->
                        sharedTitle = titleId
                    }
                )
            }

            composable(Screen.MyScreenRecords.route) { MyScreenRecords(navController) }

            composable(
                route = Screen.ChartsScreen.route + "/{activityType}",
                arguments = listOf(navArgument("activityType") { type = NavType.StringType })
            ) { backStackEntry ->
                val activityEnumProp =
                    ActivityEnum.valueOf(
                        backStackEntry.arguments?.getString("activityType") ?: ""
                    )
                ScreenCharts(
                    activityEnum = activityEnumProp,
                    viewModelCharts =
                    viewModel(
                        factory =
                        viewModelChartsFactory(
                            viewModelRecords =
                            viewModel(
                                factory =
                                ViewModelRecordFactory()
                            )
                        )
                    ),
                    navController = navController
                )
            }
        }
    }
}
