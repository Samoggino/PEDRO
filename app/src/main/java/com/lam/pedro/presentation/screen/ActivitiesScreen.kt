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

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.navigation.NavHostController
import com.lam.pedro.R
import com.lam.pedro.presentation.navigation.Screen

@Composable
fun ActivitiesScreen(
navController: NavHostController
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()  // La Row riempie tutta la larghezza disponibile
                .height(200.dp)  // Imposta l'altezza della Row
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)  // Divide equamente lo spazio con altre Box nel Row
                    .aspectRatio(1f)  // Mantiene la Box quadrata
                    .clip(RoundedCornerShape(26.dp))
                    //.background(Color(0xFFF4A9A9))
                    .background(
                        Brush.linearGradient(
                        colors = listOf(Color(0xFFEB6363), Color(0xFFF4A9A9))
                    ))
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
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                        .size(50.dp)
                )
                Text(
                    text = "Sleep",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(15.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .weight(1f)  // Divide equamente lo spazio con altre Box nel Row
                    .aspectRatio(1f)  // Mantiene la Box quadrata
                    .clip(RoundedCornerShape(26.dp))
                    //.background(Color(0xFFEBA9F4))  // Imposta lo sfondo nero
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFD750E2), Color(0xFFEBA9F4))
                        ))
                // Arrotonda i bordi
            ) {
                Icon(
                    Icons.Filled.DirectionsRun,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(15.dp)
                        .size(50.dp)
                )
                Text(
                    text = "Run",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(15.dp)
                )
            }


        }
        Row(
            modifier = Modifier
                .fillMaxWidth()  // La Row riempie tutta la larghezza disponibile
                .height(200.dp)  // Imposta l'altezza della Row
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)  // Divide equamente lo spazio con altre Box nel Row
                    .aspectRatio(1f)  // Mantiene la Box quadrata
                    .clip(RoundedCornerShape(26.dp))
                    //.background(Color(0xFFAEF4A9))  // Imposta lo sfondo nero
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF59D14F), Color(0xFFAEF4A9))
                        ))
                // Arrotonda i bordi
            ) {
                // Altri contenuti all'interno del Box, se necessario
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)  // Divide equamente lo spazio con altre Box nel Row
                    .aspectRatio(1f)  // Mantiene la Box quadrata
                    .clip(RoundedCornerShape(26.dp))
                    //.background(Color(0xFFF4E9A9))  // Imposta lo sfondo nero
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE7D252), Color(0xFFF4E9A9))
                        ))
                // Arrotonda i bordi
            ) {
                // Altri contenuti all'interno del Box, se necessario
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()  // La Row riempie tutta la larghezza disponibile
                .height(200.dp)  // Imposta l'altezza della Row
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)  // Divide equamente lo spazio con altre Box nel Row
                    .aspectRatio(1f)  // Mantiene la Box quadrata
                    .clip(RoundedCornerShape(26.dp))
                    //.background(Color(0xFFA9F4EE))  // Imposta lo sfondo nero
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF46E7DF), Color(0xFFA9F4EE))
                        ))
                // Arrotonda i bordi
            ) {
                // Altri contenuti all'interno del Box, se necessario
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)  // Divide equamente lo spazio con altre Box nel Row
                    .aspectRatio(1f)  // Mantiene la Box quadrata
                    .clip(RoundedCornerShape(26.dp))
                    //.background(Color(0xFFA9BCF4))  // Imposta lo sfondo nero
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF507CE7), Color(0xFFA9BCF4))
                        ))
                // Arrotonda i bordi
            ) {
                // Altri contenuti all'interno del Box, se necessario
            }
        }
    }


}
