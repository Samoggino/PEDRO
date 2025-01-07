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
package com.lam.pedro.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lam.pedro.R
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.presentation.navigation.BottomBar
import com.lam.pedro.presentation.navigation.PedroNavigation
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.theme.PedroTheme

const val TAG = "Health Connect sample"

@Composable
fun PedroApp(healthConnectManager: HealthConnectManager) {
    PedroTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Definire le schermate in cui la BottomBar deve essere visibile
        val showBottomNotTop = when (currentRoute) {
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
            Screen.TrainSessionScreen.route -> true

            else -> false
        }
        val titleId = when (currentRoute) {
            /* BottomBar */
            Screen.HomeScreen.route -> Screen.HomeScreen.titleId
            Screen.ActivitiesScreen.route -> Screen.ActivitiesScreen.titleId
            Screen.CommunityScreen.route -> Screen.CommunityScreen.titleId
            Screen.MoreScreen.route -> Screen.MoreScreen.titleId

            /* Profile */
            Screen.ProfileScreen.route -> Screen.ProfileScreen.titleId

            /* Activities */
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

            /* More */
            Screen.HealthConnectScreen.route -> Screen.HealthConnectScreen.titleId
            Screen.SettingScreen.route -> Screen.SettingScreen.titleId
            Screen.PrivacyPolicy.route -> Screen.PrivacyPolicy.titleId
            Screen.AboutScreen.route -> Screen.AboutScreen.titleId

            else -> R.string.app_name
        }
        Scaffold(
            floatingActionButton = {
                if (currentRoute == Screen.ActivitiesScreen.route)
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(Screen.MyScreenRecords.route) },
                        icon = { Icon(Icons.Filled.Add, "Add Activity") },
                        text = { Text(text = "New Activity") },
                        shape = RoundedCornerShape(26.dp),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        actionColor = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            bottomBar = {
                // Mostra la BottomBar solo se `showBottomBar` è true
                if (showBottomNotTop) {
                    BottomBar(
                        scope = scope,
                        navController = navController
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                PedroNavigation(
                    healthConnectManager = healthConnectManager,
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    topBarTitle = titleId
                )
            }
        }
    }
}
