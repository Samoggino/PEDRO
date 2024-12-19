package com.lam.pedro.presentation.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.lam.pedro.data.HealthConnectManager
import com.example.healthconnectsample.presentation.screen.HealthConnectScreen
import com.lam.pedro.presentation.screen.more.AboutScreen
import com.lam.pedro.presentation.screen.ActivitiesScreen
import com.lam.pedro.presentation.screen.HomeScreen
import com.lam.pedro.presentation.screen.more.loginscreen.LandingScreen
import com.lam.pedro.presentation.screen.MoreScreen
import com.lam.pedro.presentation.screen.profile.ProfileScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.GeneralActivityViewModelFactory
import com.lam.pedro.presentation.screen.activities.newActivity.NewActivityScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.cyclingscreen.CycleSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.cyclingscreen.CycleSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.runscreen.RunSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.runscreen.RunSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.trainscreen.TrainSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.trainscreen.TrainSessionViewModel
import com.lam.pedro.presentation.screen.more.SettingsScreen
import com.lam.pedro.presentation.screen.more.loginscreen.LoginScreen
import com.lam.pedro.presentation.screen.more.PrivacyPolicyScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.sleepscreen.SleepSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.sleepscreen.SleepSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.walkscreen.WalkSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.walkscreen.WalkSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.yogascreen.YogaSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivities.yogascreen.YogaSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.drivescreen.DriveSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.drivescreen.DriveSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.listenscreen.ListenSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.sitscreen.SitSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.liftscreen.LiftSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.liftscreen.WeightSessionScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.listenscreen.ListenSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.staticactivities.sitscreen.SitSessionViewModel
import com.lam.pedro.util.showExceptionSnackbar
import kotlinx.coroutines.launch

/**
 * Provides the navigation in the app.
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PedroNavigation(
    navController: NavHostController,
    healthConnectManager: HealthConnectManager,
    snackbarHostState: SnackbarHostState,
    topBarTitle: Int
) {

    val scope = rememberCoroutineScope()

    // Stack per tenere traccia delle schermate aperte
    val screenStack = remember { mutableStateListOf<String>() }

    var sharedViewModel: ActivitySessionViewModel? by remember { mutableStateOf(null) }
    var sharedColor: Color? by remember { mutableStateOf(null) }
    var sharedTitle: Int? by remember { mutableStateOf(null) }
    //var sharedActivityType: Int? by remember { mutableStateOf(null) }

    // Funzione per loggare le schermate attive
    fun logScreenStack() {
        Log.d("PedroNavigation", "Current screen stack: $screenStack")
    }

    SharedTransitionLayout {
        NavHost(navController = navController,
            startDestination = Screen.HomeScreen.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }) {
            val availability by healthConnectManager.availability
            composable(Screen.HomeScreen.route, enterTransition = {
                fadeIn(
                    animationSpec = tween(700) // Personalizza la durata dell'animazione
                )
            },
                exitTransition = {
                    fadeOut(animationSpec = tween(600)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                screenStack.add(Screen.HomeScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                HomeScreen(navController)
            }
            composable(Screen.ActivitiesScreen.route, enterTransition = {
                fadeIn(
                    animationSpec = tween(700) // Personalizza la durata dell'animazione
                )
            },
                exitTransition = {
                    fadeOut(animationSpec = tween(600)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                screenStack.add(Screen.ActivitiesScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                ActivitiesScreen(navController)
            }
            composable(Screen.MoreScreen.route, enterTransition = {
                fadeIn(
                    animationSpec = tween(700) // Personalizza la durata dell'animazione
                )
            },
                exitTransition = {
                    fadeOut(animationSpec = tween(600)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                screenStack.add(Screen.MoreScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                MoreScreen(navController)
            }
            composable(Screen.ProfileScreen.route, enterTransition = {
                fadeIn(
                    animationSpec = tween(700) // Personalizza la durata dell'animazione
                )
            },
                exitTransition = {
                    fadeOut(animationSpec = tween(600)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                screenStack.add(Screen.ProfileScreen.route)
                logScreenStack()
                ProfileScreen(navController, topBarTitle)
            }
            composable(
                Screen.AboutScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Entra da destra
                        animationSpec = tween(700) // Durata dell'animazione
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Esce verso destra
                        animationSpec = tween(600) // Durata dell'uscita
                    )
                },
            ) {
                screenStack.add(Screen.AboutScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                AboutScreen(navController = navController, titleId = topBarTitle)
            }
            composable(Screen.LoginScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Entra da destra
                        animationSpec = tween(700) // Durata dell'animazione
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Esce verso destra
                        animationSpec = tween(600) // Durata dell'uscita
                    )
                }) {
                screenStack.add(Screen.LoginScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                LoginScreen(navController)
            }
            composable(Screen.LandingScreen.route) {
                screenStack.add(Screen.LandingScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                LandingScreen()
            }
            composable(
                route = Screen.PrivacyPolicy.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Entra da destra
                        animationSpec = tween(700) // Durata dell'animazione
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Esce verso destra
                        animationSpec = tween(600) // Durata dell'uscita
                    )
                },
                deepLinks = listOf(
                    navDeepLink {
                        action = "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"
                    }
                )
            ) {
                screenStack.add(Screen.HealthConnectScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                PrivacyPolicyScreen(navController = navController, titleId = topBarTitle)
            }
            composable(Screen.HealthConnectScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Entra da destra
                        animationSpec = tween(700) // Durata dell'animazione
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Esce verso destra
                        animationSpec = tween(600) // Durata dell'uscita
                    )
                }
            ) {
                screenStack.add(Screen.HealthConnectScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                HealthConnectScreen(
                    healthConnectAvailability = availability,
                    onResumeAvailabilityCheck = {
                        healthConnectManager.checkAvailability()
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    revokeAllPermissions = { scope.launch { healthConnectManager.revokeAllPermissions() } }
                )
            }
            composable(Screen.SettingScreen.route,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Entra da destra
                        animationSpec = tween(700) // Durata dell'animazione
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Esce verso destra
                        animationSpec = tween(600) // Durata dell'uscita
                    )
                }
            ) {
                screenStack.add(Screen.SettingScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SettingsScreen(
                    navController = navController,
                    titleId = topBarTitle
                )
            }

            composable(Screen.NewActivityScreen.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000))
                }) {
                screenStack.add(Screen.NewActivityScreen.route)
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

            composable(Screen.RunSessionScreen.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(1000))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000))
                }) {
                val viewModel: RunSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.RunSessionScreen.color
                sharedTitle = Screen.RunSessionScreen.titleId
                //sharedActivityType = Screen.RunSessionScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.RunSessionScreen.activityType*/) }

                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }

                screenStack.add(Screen.RunSessionScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                RunSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Run",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.RunSessionScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.RunSessionScreen.color,
                    image = Screen.RunSessionScreen.image,
                    viewModel = viewModel
                )
            }

            composable(Screen.SleepSessions.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.SleepSessions.color
                sharedTitle = Screen.SleepSessions.titleId
                //sharedActivityType = Screen.SleepSessions.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.SleepSessions.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.SleepSessions.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SleepSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Sleep",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.SleepSessions.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.SleepSessions.color,
                    image = Screen.SleepSessions.image,
                    viewModel = viewModel
                )
            }

            composable(Screen.WalkSessionScreen.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: WalkSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.WalkSessionScreen.color
                sharedTitle = Screen.WalkSessionScreen.titleId
                //sharedActivityType = Screen.WalkSessionScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.WalkSessionScreen.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.WalkSessionScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                WalkSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Walk",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.WalkSessionScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.WalkSessionScreen.color,
                    image = Screen.WalkSessionScreen.image,
                    viewModel = viewModel
                )
            }

            composable(Screen.DriveSessionScreen.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: DriveSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.DriveSessionScreen.color
                sharedTitle = Screen.DriveSessionScreen.titleId
                //sharedActivityType = Screen.DriveSessionScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.DriveSessionScreen.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.DriveSessionScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                DriveSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Drive",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.DriveSessionScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.DriveSessionScreen.color,
                    image = Screen.DriveSessionScreen.image,
                    viewModel = viewModel
                )
            }

            composable(Screen.SitSessionScreen.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: SitSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.SitSessionScreen.color
                sharedTitle = Screen.SitSessionScreen.titleId
                //sharedActivityType = Screen.SitSessionScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.SitSessionScreen.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.SitSessionScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SitSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Sit",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.SitSessionScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.SitSessionScreen.color,
                    image = Screen.SitSessionScreen.image,
                    viewModel = viewModel
                )
            }

            composable(Screen.ListenSessionScreen.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: ListenSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.ListenSessionScreen.color
                sharedTitle = Screen.ListenSessionScreen.titleId
                //sharedActivityType = Screen.ListenSessionScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.ListenSessionScreen.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.ListenSessionScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                ListenSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Listen",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.ListenSessionScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.ListenSessionScreen.color,
                    image = Screen.ListenSessionScreen.image,
                    viewModel = viewModel
                )
            }

            composable(Screen.WeightScreen.route) {
                val viewModel: LiftSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.WeightScreen.color
                sharedTitle = Screen.WeightScreen.titleId
                //sharedActivityType = Screen.WeightScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val permissions = viewModel.permissions
                val sessionsList by viewModel.sessionsList

                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.WeightScreen.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.WeightScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                WeightSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Lift",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.WeightScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.WeightScreen.color,
                    image = Screen.WeightScreen.image,
                    viewModel = viewModel
                )
            }

            composable(Screen.YogaSessionScreen.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: YogaSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.YogaSessionScreen.color
                sharedTitle = Screen.YogaSessionScreen.titleId
                //sharedActivityType = Screen.YogaSessionScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.YogaSessionScreen.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.YogaSessionScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                YogaSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Yoga",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.YogaSessionScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.YogaSessionScreen.color,
                    image = Screen.YogaSessionScreen.image,
                    viewModel = viewModel
                )
            }


            composable(Screen.CycleSessionScreen.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: CycleSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.CycleSessionScreen.color
                sharedTitle = Screen.CycleSessionScreen.titleId
                //sharedActivityType = Screen.CycleSessionScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.CycleSessionScreen.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.CycleSessionScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                CycleSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Cycle",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.CycleSessionScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.CycleSessionScreen.color,
                    image = Screen.CycleSessionScreen.image,
                    viewModel = viewModel
                )
            }

            composable(Screen.TrainSessionScreen.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: TrainSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )

                sharedViewModel = viewModel
                sharedColor = Screen.TrainSessionScreen.color
                sharedTitle = Screen.TrainSessionScreen.titleId
                //sharedActivityType = Screen.TrainSessionScreen.activityType

                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult =
                    { viewModel.initialLoad(viewModel.activityType/*Screen.TrainSessionScreen.activityType*/) }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                screenStack.add(Screen.TrainSessionScreen.route)
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                TrainSessionScreen(
                    permissions = permissions,
                    permissionsGranted = permissionsGranted,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.startRecording(
                            "My Train",
                            "Notes"
                        )
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad(viewModel.activityType/*Screen.TrainSessionScreen.activityType*/)
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.TrainSessionScreen.color,
                    image = Screen.TrainSessionScreen.image,
                    viewModel = viewModel
                )
            }

        }
    }
}
