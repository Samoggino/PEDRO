package com.lam.pedro.presentation.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
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
import com.example.healthconnectsample.data.HealthConnectManager
import com.example.healthconnectsample.presentation.screen.HealthConnectScreen
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.presentation.charts.ScreenCharts
import com.lam.pedro.presentation.following.FollowScreen
import com.lam.pedro.presentation.screen.AboutScreen
import com.lam.pedro.presentation.screen.ActivitiesScreen
import com.lam.pedro.presentation.screen.HomeScreen
import com.lam.pedro.presentation.screen.LandingScreen
import com.lam.pedro.presentation.screen.MoreScreen
import com.lam.pedro.presentation.screen.SettingsScreen
import com.lam.pedro.presentation.screen.changes.DifferentialChangesScreen
import com.lam.pedro.presentation.screen.changes.DifferentialChangesViewModel
import com.lam.pedro.presentation.screen.changes.DifferentialChangesViewModelFactory
import com.lam.pedro.presentation.screen.exercisesession.ExerciseSessionScreen
import com.lam.pedro.presentation.screen.exercisesession.ExerciseSessionViewModel
import com.lam.pedro.presentation.screen.exercisesession.ExerciseSessionViewModelFactory
import com.lam.pedro.presentation.screen.exercisesessiondetail.ExerciseSessionDetailScreen
import com.lam.pedro.presentation.screen.exercisesessiondetail.ExerciseSessionDetailViewModel
import com.lam.pedro.presentation.screen.exercisesessiondetail.ExerciseSessionDetailViewModelFactory
import com.lam.pedro.presentation.screen.inputreadings.InputReadingsScreen
import com.lam.pedro.presentation.screen.inputreadings.InputReadingsViewModel
import com.lam.pedro.presentation.screen.inputreadings.InputReadingsViewModelFactory
import com.lam.pedro.presentation.screen.loginscreen.LoginScreen
import com.lam.pedro.presentation.screen.privacypolicy.PrivacyPolicyScreen
import com.lam.pedro.presentation.screen.recordlist.RecordListScreen
import com.lam.pedro.presentation.screen.recordlist.RecordListScreenViewModel
import com.lam.pedro.presentation.screen.recordlist.RecordListViewModelFactory
import com.lam.pedro.presentation.screen.recordlist.RecordType
import com.lam.pedro.presentation.screen.recordlist.SeriesRecordsType
import com.lam.pedro.presentation.screen.sleepsession.SleepSessionScreen
import com.lam.pedro.presentation.screen.sleepsession.SleepSessionViewModel
import com.lam.pedro.presentation.screen.sleepsession.SleepSessionViewModelFactory
import com.lam.pedro.presentation.serialization.MyScreenRecords
import com.lam.pedro.showExceptionSnackbar
import kotlinx.coroutines.launch

/**
 * Provides the navigation in the app.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PedroNavigation(
    navController: NavHostController,
    healthConnectManager: HealthConnectManager,
    snackbarHostState: SnackbarHostState,
    topBarTitle: Int
) {

    val scope = rememberCoroutineScope()
    NavHost(navController = navController,
        startDestination = Screen.MyScreenRecords.route,
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

        composable(Screen.MyScreenRecords.route) {
            MyScreenRecords(navController)
        }

        composable(Screen.FollowScreen.route) {
            FollowScreen()
        }

        composable(Screen.ChartsScreen.route) {
            ScreenCharts(
                activityType = ActivityType.CYCLING,
            )
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
        composable(Screen.ExerciseSessions.route) {
            val viewModel: ExerciseSessionViewModel = viewModel(
                factory = ExerciseSessionViewModelFactory(
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
            ExerciseSessionScreen(
                permissionsGranted = permissionsGranted,
                permissions = permissions,
                sessionsList = sessionsList,
                uiState = viewModel.uiState,
                onInsertClick = {
                    viewModel.insertExerciseSession()
                },
                onDetailsClick = { uid ->
                    navController.navigate(Screen.ExerciseSessionDetail.route + "/" + uid)
                },
                onDeleteClick = { uid ->
                    viewModel.deleteExerciseSession(uid)
                },
                onError = { exception ->
                    showExceptionSnackbar(snackbarHostState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
        composable(Screen.ExerciseSessionDetail.route + "/{$UID_NAV_ARGUMENT}") {
            val uid = it.arguments?.getString(UID_NAV_ARGUMENT)!!
            val viewModel: ExerciseSessionDetailViewModel = viewModel(
                factory = ExerciseSessionDetailViewModelFactory(
                    uid = uid,
                    healthConnectManager = healthConnectManager
                )
            )
            val permissionsGranted by viewModel.permissionsGranted
            val sessionMetrics by viewModel.sessionMetrics
            val permissions = viewModel.permissions
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    onPermissionsResult()
                }
            ExerciseSessionDetailScreen(
                permissions = permissions,
                permissionsGranted = permissionsGranted,
                sessionMetrics = sessionMetrics,
                uiState = viewModel.uiState,
                onDetailsClick = { recordType, uid, seriesRecordsType ->
                    navController.navigate(Screen.RecordListScreen.route + "/" + recordType + "/" + uid + "/" + seriesRecordsType)
                },
                onError = { exception ->
                    showExceptionSnackbar(snackbarHostState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
        composable(Screen.RecordListScreen.route + "/{$RECORD_TYPE}" + "/{$UID_NAV_ARGUMENT}" + "/{$SERIES_RECORDS_TYPE}") {
            val uid = it.arguments?.getString(UID_NAV_ARGUMENT)!!
            val recordTypeString = it.arguments?.getString(RECORD_TYPE)!!
            val seriesRecordsTypeString = it.arguments?.getString(SERIES_RECORDS_TYPE)!!
            val viewModel: RecordListScreenViewModel = viewModel(
                factory = RecordListViewModelFactory(
                    uid = uid,
                    recordTypeString = recordTypeString,
                    seriesRecordsTypeString = seriesRecordsTypeString,
                    healthConnectManager = healthConnectManager
                )
            )
            val permissionsGranted by viewModel.permissionsGranted
            val recordList = viewModel.recordList
            val permissions = viewModel.permissions
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    onPermissionsResult()
                }
            RecordListScreen(
                uid = uid,
                permissions = permissions,
                permissionsGranted = permissionsGranted,
                recordType = RecordType.valueOf(recordTypeString),
                seriesRecordsType = SeriesRecordsType.valueOf(seriesRecordsTypeString),
                recordList = recordList,
                uiState = viewModel.uiState,
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
        composable(Screen.SleepSessions.route,
            enterTransition = {
                scaleIn(
                    animationSpec = tween(1000) // Personalizza la durata dell'animazione
                )
            },
            exitTransition = {
                shrinkOut(animationSpec = tween(1000)) // Aggiungi un'animazione di uscita, se desiderato
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
                    viewModel.generateSleepData()
                },
                onError = { exception ->
                    showExceptionSnackbar(snackbarHostState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                },
                onPermissionsLaunch = { values ->
                    permissionsLauncher.launch(values)
                }
            )
        }
        composable(Screen.InputReadings.route) {
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
            InputReadingsScreen(
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
                }
            )
        }
        composable(Screen.DifferentialChanges.route) {
            val viewModel: DifferentialChangesViewModel = viewModel(
                factory = DifferentialChangesViewModelFactory(
                    healthConnectManager = healthConnectManager
                )
            )
            val changesToken by viewModel.changesToken
            val permissionsGranted by viewModel.permissionsGranted
            val permissions = viewModel.permissions
            val onPermissionsResult = { viewModel.initialLoad() }
            val permissionsLauncher =
                rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
                    onPermissionsResult()
                }
            DifferentialChangesScreen(
                permissionsGranted = permissionsGranted,
                permissions = permissions,
                changesEnabled = changesToken != null,
                onChangesEnable = { enabled ->
                    viewModel.enableOrDisableChanges(enabled)
                },
                changes = viewModel.changes,
                changesToken = changesToken,
                onGetChanges = {
                    viewModel.getChanges()
                },
                uiState = viewModel.uiState,
                onError = { exception ->
                    showExceptionSnackbar(snackbarHostState, scope, exception)
                },
                onPermissionsResult = {
                    viewModel.initialLoad()
                }
            ) { values ->
                permissionsLauncher.launch(values)
            }
        }
    }
}
