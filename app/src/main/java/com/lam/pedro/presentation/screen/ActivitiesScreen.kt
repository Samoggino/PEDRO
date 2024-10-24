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
package com.lam.pedro.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.ChairAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lam.pedro.presentation.navigation.Screen

@Composable
fun ActivitiesScreen(
    navController: NavHostController
) {
    //val context = LocalContext.current
    val scrollState = rememberScrollState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Static Activities", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Imposta l'altezza della Row
        ) {
            Box(
                modifier = Modifier
                    .height(100.dp)  // Divide equamente lo spazio con altre Box nel Row
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xff74c9c6))
                    .clickable(onClick = {
                        navController.navigate(Screen.SleepSessions.route) {
                            // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        } // Cambia lo stato del click
                    })
            ) {
                Icon(
                    Icons.Filled.Bed,
                    contentDescription = null,
                    tint = Color(0x80FFFFFF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(120.dp)
                )
                Text(
                    text = "Sleep",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                )

            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .height(100.dp)  // Divide equamente lo spazio con altre Box nel Row
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF61a6f1))
                    .clickable(onClick = {
                        navController.navigate(Screen.SleepSessions.route) {
                            // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        } // Cambia lo stato del click
                    })
            ) {
                Icon(
                    Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    tint = Color(0x80FFFFFF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(120.dp)
                )
                Text(
                    text = "Drive",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                )

            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Imposta l'altezza della Row
        ) {
            Box(
                modifier = Modifier
                    .height(100.dp)  // Divide equamente lo spazio con altre Box nel Row
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xff71c97b))
                    .clickable(onClick = {
                        navController.navigate(Screen.SleepSessions.route) {
                            // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        } // Cambia lo stato del click
                    })
            ) {
                Icon(
                    Icons.Filled.ChairAlt,
                    contentDescription = null,
                    tint = Color(0x80FFFFFF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(120.dp)
                )
                Text(
                    text = "Sit",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                )

            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .height(100.dp)  // Divide equamente lo spazio con altre Box nel Row
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF7771C9))
                    .clickable(onClick = {
                        navController.navigate(Screen.InputReadings.route) {
                            // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        } // Cambia lo stato del click
                    })
            ) {
                Icon(
                    Icons.Filled.Album,
                    contentDescription = null,
                    tint = Color(0x80FFFFFF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(120.dp)
                )
                Text(
                    text = "Weight",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                )

            }

        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(text = "Dynamic Activities", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Imposta l'altezza della Row
        ) {


            Box(
                modifier = Modifier
                    .height(100.dp)  // Divide equamente lo spazio con altre Box nel Row
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFf87757))  // Imposta lo sfondo nero
                    .clickable(onClick = {
                        navController.navigate(Screen.ExerciseSessions.route) {
                            // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        } // Cambia lo stato del click
                    })
            ) {
                Icon(
                    Icons.Filled.DirectionsRun,
                    contentDescription = null,
                    tint = Color(0x80FFFFFF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(120.dp)
                )
                Text(
                    text = "Run",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(100.dp)  // Divide equamente lo spazio con altre Box nel Row
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFfaaf5a))
            ) {
                Icon(
                    Icons.Filled.DirectionsWalk,
                    contentDescription = null,
                    tint = Color(0x80FFFFFF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(120.dp)
                )
                Text(
                    text = "Walk",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                )
            }


        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()  // La Row riempie tutta la larghezza disponibile
                .height(200.dp)  // Imposta l'altezza della Row
        ) {

            Box(
                modifier = Modifier
                    .height(100.dp)  // Divide equamente lo spazio con altre Box nel Row
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFFad71c9))  // Imposta lo sfondo nero
                /*
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF2D2D2D), Color(0xFF2D2D2D))
                    )
                )

                 */
                // Arrotonda i bordi
            ) {
                Icon(
                    Icons.Filled.SportsGymnastics,
                    contentDescription = null,
                    tint = Color(0x80FFFFFF),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(120.dp)
                )
                Text(
                    text = "Yoga",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)  // Divide equamente lo spazio con altre Box nel Row
                    .aspectRatio(1f)
            ) {

            }


        }
        Spacer(modifier = Modifier.height(30.dp))


    }
}
