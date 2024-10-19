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
package com.lam.pedro.ui.navigation

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lam.pedro.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val HEALTH_CONNECT_SETTINGS_ACTION = "androidx.health.ACTION_HEALTH_CONNECT_SETTINGS"

/**
 * The side navigation drawer used to explore each Health Connect feature.
 */
@Composable
fun Drawer(
    scope: CoroutineScope,
    drawerState: DrawerState, // Cambiato da ScaffoldState a DrawerState
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val activity = LocalContext.current
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .width(96.dp)
                    .clickable {
                        navController.navigate(Screen.WelcomeScreen.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch {
                            drawerState.close() // Usa drawerState per chiudere il drawer
                        }
                    },
                painter = painterResource(id = R.drawable.ic_health_connect_logo),
                contentDescription = stringResource(id = R.string.health_connect_logo)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.app_name)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Screen.values().filter { it.hasMenuItem }.forEach { item ->
            DrawerItem(
                item = item,
                selected = item.route == currentRoute,
                onItemClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scope.launch {
                        drawerState.close() // Usa drawerState per chiudere il drawer
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        val settingsIntent = Intent()
                        settingsIntent.action = HEALTH_CONNECT_SETTINGS_ACTION
                        activity.startActivity(settingsIntent)
                    }
                )
                .height(48.dp)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DrawerPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    HealthConnectTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Drawer(
                    scope = scope,
                    navController = navController,
                    scaffoldState = TODO()
                )
            },
            content = {
                // Puoi aggiungere un contenuto qui se necessario
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Drawer Preview") })
                    }
                ) { paddingValues ->
                    // Inserisci qui il tuo contenuto principale
                    // Ad esempio, potresti voler visualizzare un messaggio
                    Text("Contenuto principale", modifier = Modifier.padding(paddingValues))
                }
            }
        )
    }
}


 */