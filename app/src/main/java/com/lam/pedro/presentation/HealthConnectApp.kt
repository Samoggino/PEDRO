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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_AVAILABLE
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthconnectsample.data.HealthConnectManager
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.R
import com.lam.pedro.presentation.navigation.BottomBar
import com.lam.pedro.presentation.navigation.PedroNavigation
import com.lam.pedro.presentation.theme.HealthConnectTheme
import kotlinx.coroutines.launch

const val TAG = "Health Connect sample"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectApp(healthConnectManager: HealthConnectManager) {
    HealthConnectTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val availability by healthConnectManager.availability

        // Definire le schermate in cui la BottomBar deve essere visibile
        val showBottomNotTop = when (currentRoute) {
            Screen.HomeScreen.route,
            Screen.MoreScreen.route,
            Screen.ActivitiesScreen.route -> true

            else -> false
        }

        Scaffold(
            topBar = {if (showBottomNotTop) { //Do nothing
            } else {
                TopAppBar(
                    title = {
                        val titleId = when (currentRoute) {
                            Screen.ExerciseSessions.route -> Screen.ExerciseSessions.titleId
                            Screen.SleepSessions.route -> Screen.SleepSessions.titleId
                            Screen.InputReadings.route -> Screen.InputReadings.titleId
                            Screen.DifferentialChanges.route -> Screen.DifferentialChanges.titleId
                            Screen.HealthConnectScreen.route -> Screen.HealthConnectScreen.titleId
                            Screen.ActivitiesScreen.route -> Screen.ActivitiesScreen.titleId
                            Screen.HomeScreen.route -> Screen.HomeScreen.titleId
                            Screen.MoreScreen.route -> Screen.MoreScreen.titleId
                            Screen.PrivacyPolicy.route -> Screen.PrivacyPolicy.titleId

                            else -> R.string.app_name
                        }
                        Text(
                            text = stringResource(titleId),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        // Mostra la freccia indietro nelle schermate dove la BottomBar non è visibile
                        if (!showBottomNotTop) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White.copy(alpha = 0f)
                    )
                )
            }
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
                snackbarHostState = snackbarHostState
            )
        }
    }
}
}
