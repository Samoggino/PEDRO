package com.lam.pedro.presentation.navigation

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.lam.pedro.R
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.datasource.SecurePreferencesManager.getMyContext
import com.lam.pedro.presentation.screen.ActivitiesScreen
import com.lam.pedro.presentation.screen.HomeScreen
import com.lam.pedro.presentation.screen.MoreScreen
import com.lam.pedro.presentation.screen.activities.activitiyscreens.ActivitySessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.GeneralActivityViewModelFactory
import com.lam.pedro.presentation.screen.activities.activitiyscreens.dynamicactivitiesviewmodels.RunSessionViewModel
import com.lam.pedro.presentation.screen.activities.activitiyscreens.unknownactivityviewmodel.UnknownSessionViewModel
import com.lam.pedro.presentation.screen.activities.newActivity.NewActivityScreen
import com.lam.pedro.presentation.screen.community.CommunityScreen
import com.lam.pedro.presentation.screen.community.CommunityScreenViewModelFactory
import com.lam.pedro.presentation.screen.community.chat.ChatScreen
import com.lam.pedro.presentation.screen.community.user.UserCommunityDetails
import com.lam.pedro.presentation.screen.more.AboutScreen
import com.lam.pedro.presentation.screen.more.HealthConnectScreen
import com.lam.pedro.presentation.screen.more.PrivacyPolicyScreen
import com.lam.pedro.presentation.screen.more.loginscreen.LoginScreen
import com.lam.pedro.presentation.screen.more.loginscreen.RegisterScreen
import com.lam.pedro.presentation.screen.more.loginscreen.User
import com.lam.pedro.presentation.screen.more.settingsscreen.SettingsScreen
import com.lam.pedro.presentation.screen.profile.ProfileScreen
import com.lam.pedro.presentation.serialization.MyScreenRecords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Provides the navigation in the app. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PedroNavigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // Mappa costruita dinamicamente dagli elementi dell'enum Screen
    val routeToTitleMap = Screen.entries.associate { it.route to it.titleId }

    // Funzione per ottenere il titleId associato al route
    fun getTitleIdForRoute(currentRoute: String?): Int {
        Log.d("Navigation", "getTitleIdForRoute $currentRoute")
        return routeToTitleMap[currentRoute] ?: R.string.app_name
    }

    fun onNavBack() {
        navController.popBackStack()
    }

    Log.d("Navigation", "PedroNavigation")

    val scope = rememberCoroutineScope()

//     Stack per tenere traccia delle schermate aperte

    var sharedViewModel: ActivitySessionViewModel? by remember { mutableStateOf(null) }
    var sharedTitle: Int? = 0

    //     Funzione per loggare le schermate attive
    fun logScreenStack() {
        Log.d("Reload", "Ho cambiato pagina")
    }

//    val context = LocalContext.current

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
                enterTransition = { NavigationTransitions.fadeIn(700) },
                exitTransition = { NavigationTransitions.fadeOut(700) }
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
                    onLoginClick = { navController.navigate(Screen.LoginScreen.route) },
                    viewModel = viewModel(factory = CommunityScreenViewModelFactory())
                )

            }

            composable(
                route = Screen.HomeScreen.route,
                enterTransition = { NavigationTransitions.fadeIn(700) },
                exitTransition = { NavigationTransitions.fadeOut(700) }
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                Log.d("Navigation", "HomeScreen navigation")
                HomeScreen(onProfileClick = { navController.navigate(Screen.ProfileScreen.route) })
            }
            composable(
                route = Screen.ActivitiesScreen.route,
                enterTransition = { NavigationTransitions.fadeIn(700) },
                exitTransition = { NavigationTransitions.fadeOut(700) }
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                ActivitiesScreen(
                    onActivityItemClick = { activity ->
                        activity.route?.let { navController.navigate(it) }
                    }
                )
            }

            composable(
                route = Screen.MoreScreen.route,
                enterTransition = { NavigationTransitions.fadeIn(700) },
                exitTransition = { NavigationTransitions.fadeOut(700) }
            ) {
                Log.d("BackStack", "Stack: ${navController.currentBackStackEntry}")
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                MoreScreen(onNavigate = { route ->
                    navController.navigate(route)
                })
            }
            composable(
                route = Screen.ProfileScreen.route,
                enterTransition = { NavigationTransitions.fadeIn(700) },
                exitTransition = { NavigationTransitions.fadeOut(700) }
            ) {
                logScreenStack()
                ProfileScreen(
                    titleId = getTitleIdForRoute(currentRoute),
                    onNavBack = { onNavBack() },
                )
            }
            composable(
                route = Screen.AboutScreen.route,
                enterTransition = { NavigationTransitions.slideInHorizontally() },
                exitTransition = { NavigationTransitions.slideOutHorizontally() },
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                AboutScreen(titleId = getTitleIdForRoute(currentRoute), onNavBack = { onNavBack() })
            }
            composable(
                route = Screen.LoginScreen.route,
                enterTransition = { NavigationTransitions.slideInHorizontally() },
                exitTransition = { NavigationTransitions.slideOutHorizontally() }
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                LoginScreen(
                    onNavBack = { onNavBack() },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(
                route = Screen.RegisterScreen.route,
                enterTransition = { NavigationTransitions.slideInHorizontally() },
                exitTransition = { NavigationTransitions.slideOutHorizontally() }
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                RegisterScreen(
                    onNavBack = { onNavBack() },
                    onNavigate = { route -> navController.navigate(route) }
                )
            }


            composable(
                route = Screen.PrivacyPolicy.route,
                enterTransition = { NavigationTransitions.slideInHorizontally() },
                exitTransition = { NavigationTransitions.slideOutHorizontally() },
                deepLinks =
                listOf(
                    navDeepLink {
                        action = "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"
                    }
                )
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                PrivacyPolicyScreen(
                    onNavBack = { onNavBack() },
                    titleId = getTitleIdForRoute(currentRoute)
                )
            }
            composable(
                route = Screen.HealthConnectScreen.route,
                enterTransition = { NavigationTransitions.slideInHorizontally() },
                exitTransition = { NavigationTransitions.slideOutHorizontally() }
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                HealthConnectScreen(
                    healthConnectAvailability = availability,
                    onResumeAvailabilityCheck = { healthConnectManager.checkAvailability() },
                    onNavBack = { onNavBack() },
                    titleId = getTitleIdForRoute(currentRoute),
                    revokeAllPermissions = {
                        scope.launch(Dispatchers.IO) { healthConnectManager.revokeAllPermissions() }
                    }
                )
            }
            composable(
                route = Screen.SettingScreen.route,
                enterTransition = { NavigationTransitions.slideInHorizontally() },
                exitTransition = { NavigationTransitions.slideOutHorizontally() }
            ) {
                logScreenStack() // Log dello stack dopo aver aperto la schermata
                SettingsScreen(
                    onNavBack = { onNavBack() },
                    titleId = getTitleIdForRoute(currentRoute),
                    onNavigate = { route -> navController.navigate(route) }
                )
            }

            composable(
                Screen.NewActivityScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                logScreenStack()

                val viewModel = sharedViewModel
                val screenTitleId = sharedTitle

                if (viewModel != null && screenTitleId != null) {
                    NewActivityScreen(
                        onNavBack = { onNavBack() },
                        titleId = screenTitleId,
                        viewModel = viewModel
                    )
                }
            }

            composable(
                Screen.UnknownSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                val activityViewModel: UnknownSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory()
                )
                SetupSessionScreen(
                    screen = Screen.UnknownSessionScreen,
                    activityViewModel = activityViewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.RunSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                val activityViewModel: RunSessionViewModel = viewModel(
                    factory = GeneralActivityViewModelFactory()
                )
                SetupSessionScreen(
                    screen = Screen.RunSessionScreen,
                    activityViewModel = activityViewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }
            composable(
                Screen.SleepSessions.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.SleepSessions,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.WalkSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.WalkSessionScreen,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.DriveSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.DriveSessionScreen,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.SitSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.SitSessionScreen,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.ListenSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.ListenSessionScreen,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.WeightScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.WeightScreen,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.YogaSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.YogaSessionScreen,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.CycleSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.CycleSessionScreen,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }

            composable(
                Screen.TrainSessionScreen.route,
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() }
            ) {
                SetupSessionScreen(
                    screen = Screen.TrainSessionScreen,
                    activityViewModel = viewModel(factory = GeneralActivityViewModelFactory()),
                    onNavigate = { route -> navController.navigate(route) },
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    topBarTitle = getTitleIdForRoute(currentRoute),
                    onSharedViewModelChange = { viewModel -> sharedViewModel = viewModel },
                    onSharedTitleChange = { titleId -> sharedTitle = titleId }
                )
            }
            composable(Screen.MyScreenRecords.route) {
                MyScreenRecords(
                    onNavBack = { onNavBack() },
                    onCommunityClick = { navController.navigate(Screen.CommunityScreen.route) }
                )
            }


            composable(
                route = Screen.CommunityUserDetails.route + "/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                if (userId != null) {
                    UserCommunityDetails(
                        selectedUser = userId,
                        onNavBack = { onNavBack() })
                }
            }

            composable(
                route = Screen.ChatScreen.route + "/{currentUser}",
                enterTransition = { NavigationTransitions.fadeIn() },
                exitTransition = { NavigationTransitions.fadeOut() },
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

