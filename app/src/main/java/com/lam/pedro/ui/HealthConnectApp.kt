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
package com.lam.pedro.ui

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lam.pedro.model.data.HealthConnectAvailability
import com.lam.pedro.model.data.HealthConnectManager
import com.lam.pedro.ui.navigation.Drawer
import com.lam.pedro.ui.navigation.HealthConnectNavigation
import com.lam.pedro.ui.navigation.Screen
import com.lam.pedro.ui.theme.HealthConnectTheme
import com.lam.pedro.R
import kotlinx.coroutines.launch

const val TAG = "Health Connect Codelab"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HealthConnectApp(healthConnectManager: HealthConnectManager) {
  HealthConnectTheme {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val availability by healthConnectManager.availability

    ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        if (availability == HealthConnectAvailability.INSTALLED) {
          Drawer(
              scope = TODO(),
              drawerState = TODO(),
              navController = TODO()
          )
        }
      },
      content = {
        Scaffold(
          snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
          topBar = {
            TopAppBar(
              title = {
                val titleId = when (currentRoute) {
                  Screen.ExerciseSessions.route -> Screen.ExerciseSessions.titleId
                  Screen.InputReadings.route -> Screen.InputReadings.titleId
                  Screen.DifferentialChanges.route -> Screen.DifferentialChanges.titleId
                  else -> R.string.app_name
                }
                Text(stringResource(titleId))
              },
              navigationIcon = {
                IconButton(
                  onClick = {
                    if (availability == HealthConnectAvailability.INSTALLED) {
                      scope.launch {
                        drawerState.open()
                      }
                    }
                  }
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = stringResource(id = R.string.menu)
                  )
                }
              }
            )
          },
        ) {
          HealthConnectNavigation(
              healthConnectManager = healthConnectManager,
              navController = navController,
              snackbarHostState = TODO()
          )
        }
      }
    )
  }
}
