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

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.lam.pedro.R

/**
 * The side navigation drawer used to explore each Health Connect feature.
 */
@Composable
fun BottomBar(navController: NavController, currentRoute: String?) {

//    val navBackStackEntry = navController.currentBackStackEntryAsState().value
//    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.05f),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Screen.entries.filter { (it.hasMenuItem) }.forEach { item ->

            val selected = item.route == currentRoute
            NavigationBarItem(
                icon = {
                    when (item.titleId) {
                        R.string.home_screen -> Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home",
                        )

                        R.string.activity_list -> Icon(
                            Icons.Filled.SpaceDashboard,
                            contentDescription = "Activities",
                        )

                        R.string.community_screen -> Icon(
                            Icons.Filled.PeopleAlt,
                            contentDescription = "Community",
                        )

                        R.string.more_screen -> Icon(
                            Icons.Filled.MoreHoriz,
                            contentDescription = "More",
                        )
                    }
                },

                label = {
                    Text(
                        text = stringResource(item.titleId),
                        fontWeight = if (selected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
                selected = item.route == currentRoute,
                onClick = {
                    Log.d("BottomBar", "Navigating to ${item.route}")
                    navController.navigate(item.route)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary
                )// Chiama la funzione di navigazione per Home
            )
        }
    }

}
