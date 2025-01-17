package com.lam.pedro.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lam.pedro.presentation.navigation.BottomBar
import com.lam.pedro.presentation.navigation.PedroNavigation
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.theme.PedroTheme

const val TAG = "Health Connect sample"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PedroApp() {

    Log.i("PedroApp", "PedroApp reloaded")
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    // Definire le schermate in cui la BottomBar deve essere visibile
    val showBottomNotTop = remember(currentRoute) {
        derivedStateOf {
            when (currentRoute) {
                Screen.HomeScreen.route,
                Screen.MoreScreen.route,
                Screen.CommunityScreen.route,
                Screen.ActivitiesScreen.route,

                Screen.SleepSessions.route,
                Screen.DriveSessionScreen.route,
                Screen.SitSessionScreen.route,
                Screen.WeightScreen.route,
                Screen.ListenSessionScreen.route,

                Screen.RunSessionScreen.route,
                Screen.WalkSessionScreen.route,
                Screen.YogaSessionScreen.route,
                Screen.CycleSessionScreen.route,
                Screen.TrainSessionScreen.route,
                Screen.UnknownSessionScreen.route -> true

                else -> false
            }

        }
    }

    PedroTheme {
        Scaffold(
            floatingActionButton = {
                if (currentRoute == Screen.ActivitiesScreen.route) {
                    ExtendedFloatingActionButton(
                        // TODO: da togliere, ovviamente
                        onClick = { navController.navigate(Screen.MyScreenRecords.route) },
                        icon = { Icon(Icons.Filled.Add, "Add Activity") },
                        text = { Text(text = "New Activity") },
                        shape = RoundedCornerShape(26.dp),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                // Mostra la BottomBar solo se `showBottomBar` è true
                if (showBottomNotTop.value) {
                    BottomBar(
                        currentRoute = currentRoute,
                        onNavigateToHome = { navController.navigate(Screen.HomeScreen.route) },
                        onNavigateToActivities = { navController.navigate(Screen.ActivitiesScreen.route) },
                        onNavigateToCommunity = { navController.navigate(Screen.CommunityScreen.route) },
                        onNavigateToMore = { navController.navigate(Screen.MoreScreen.route) }
                    )
                }
            }
        ) {

            Box(modifier = Modifier.padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 60.dp)) {
                PedroNavigation(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                )
            }
        }

    }
}