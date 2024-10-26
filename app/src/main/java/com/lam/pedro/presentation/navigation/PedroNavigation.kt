/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lam.pedro.presentation.navigation

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.lam.pedro.presentation.screen.activities.dynamicactivities.cyclingscreen.CycleSessionScreen
import com.lam.pedro.presentation.screen.activities.dynamicactivities.trainscreen.TrainSessionScreen
import com.lam.pedro.presentation.screen.more.SettingsScreen
import com.lam.pedro.presentation.screen.activities.staticactivities.weightscreen.InputReadingsViewModel
import com.lam.pedro.presentation.screen.activities.staticactivities.weightscreen.InputReadingsViewModelFactory
import com.lam.pedro.presentation.screen.more.loginscreen.LoginScreen
import com.lam.pedro.presentation.screen.more.PrivacyPolicyScreen
import com.lam.pedro.presentation.screen.activities.staticactivities.sleepscreen.SleepSessionScreen
import com.lam.pedro.presentation.screen.activities.staticactivities.sleepscreen.SleepSessionViewModel
import com.lam.pedro.presentation.screen.activities.staticactivities.sleepscreen.SleepSessionViewModelFactory
import com.lam.pedro.presentation.screen.activities.dynamicactivities.walkscreen.WalkSessionScreen
import com.lam.pedro.presentation.screen.activities.dynamicactivities.yogascreen.YogaSessionScreen
import com.lam.pedro.presentation.screen.activities.staticactivities.drivescreen.DriveSessionScreen
import com.lam.pedro.presentation.screen.activities.staticactivities.listenscreen.ListenSessionScreen
import com.lam.pedro.presentation.screen.activities.staticactivities.sitscreen.SitSessionScreen
import com.lam.pedro.presentation.screen.activities.staticactivities.weightscreen.WeightSessionScreen
import com.lam.pedro.showExceptionSnackbar
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
                HomeScreen()
            }
            composable(Screen.ActivitiesScreen.route, enterTransition = {
                fadeIn(
                    animationSpec = tween(700) // Personalizza la durata dell'animazione
                )
            },
                exitTransition = {
                    fadeOut(animationSpec = tween(600)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
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
                MoreScreen(navController)
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
                AboutScreen(navController = navController, titleId = topBarTitle)
            }
            composable(Screen.LoginScreen.route) {
                LoginScreen(navController)
            }
            composable(Screen.LandingScreen.route) {
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
                SettingsScreen(
                    navController = navController,
                    titleId = topBarTitle
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
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                SleepSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.SleepSessions.color
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
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                //val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                WalkSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    //sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.WalkSessionScreen.color
                )
            }

            composable(Screen.RunSessionScreen.route,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(1000) // Personalizza la durata dell'animazione
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
                }) {
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                //val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                WalkSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    //sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.RunSessionScreen.color
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
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                //val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                DriveSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    //sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.DriveSessionScreen.color
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
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                //val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                SitSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    //sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.SitSessionScreen.color
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
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                //val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                ListenSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    //sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.ListenSessionScreen.color
                )
            }

            composable(Screen.WeightScreen.route) {
                val viewModel: InputReadingsViewModel = viewModel(
                    factory = InputReadingsViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                val readingsList by viewModel.readingsList
                val permissions = viewModel.permissions
                val weeklyAvg by viewModel.weeklyAvg
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                WeightSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,

                    uiState = viewModel.uiState,
                    onInsertClick = { weightInput ->
                        viewModel.inputReadings(weightInput)
                    },
                    weeklyAvg = weeklyAvg,
                    onDeleteClick = { uid ->
                        viewModel.deleteWeightInput(uid)
                    },
                    readingsList = readingsList,
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.WeightScreen.color
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
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                //val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                YogaSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    //sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.YogaSessionScreen.color
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
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                //val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                CycleSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    //sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.CycleSessionScreen.color
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
                val viewModel: SleepSessionViewModel = viewModel(
                    factory = SleepSessionViewModelFactory(
                        healthConnectManager = healthConnectManager
                    )
                )
                val permissionsGranted by viewModel.permissionsGranted
                //val sessionsList by viewModel.sessionsList
                val permissions = viewModel.permissions
                val onPermissionsResult = { viewModel.initialLoad() }
                val permissionsLauncher =
                    rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                        onPermissionsResult()
                    }
                TrainSessionScreen(
                    permissionsGranted = permissionsGranted,
                    permissions = permissions,
                    //sessionsList = sessionsList,
                    uiState = viewModel.uiState,
                    onInsertClick = {
                        viewModel.addSleepData()
                    },
                    onError = { exception ->
                        showExceptionSnackbar(snackbarHostState, scope, exception)
                    },
                    onPermissionsResult = {
                        viewModel.initialLoad()
                    },
                    onPermissionsLaunch = { values ->
                        permissionsLauncher.launch(values)
                    },
                    navController = navController,
                    titleId = topBarTitle,
                    color = Screen.TrainSessionScreen.color
                )
            }

        }
    }
}
