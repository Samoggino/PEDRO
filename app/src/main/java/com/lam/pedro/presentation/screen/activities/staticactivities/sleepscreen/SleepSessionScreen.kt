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
package com.lam.pedro.presentation.screen.activities.staticactivities.sleepscreen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lam.pedro.data.SleepSessionData
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.PermissionRequired
import com.lam.pedro.presentation.component.SleepSessionRow
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.lam.pedro.R

/**
 * Shows a week's worth of sleep data.
 */
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SleepSessionScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    sessionsList: List<SleepSessionData>,
    uiState: SleepSessionViewModel.UiState,
    onInsertClick: () -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {},
    navController: NavController,
    titleId: Int,
    color: Color
) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }
    // Per memorizzare lo stato del bottone e il tempo di inizio della sessione
    val isRecording = rememberSaveable { mutableStateOf(false) }
    val startTime = rememberSaveable { mutableStateOf<Instant?>(null) }

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is SleepSessionViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [SleepSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (uiState is SleepSessionViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = stringResource(titleId),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                navigationIcon = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
    if (uiState != SleepSessionViewModel.UiState.Uninitialized) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            if (!permissionsGranted) {
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }

                item {
                    PermissionRequired(color) { onPermissionsLaunch(permissions) }
                }
            } else {

                // Button per Start/Stop della registrazione
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(4.dp),
                        onClick = {
                            if (isRecording.value) {
                                // Ferma la registrazione
                                val endTime = Instant.now()
                                val newSession = SleepSessionData(
                                    uid = UUID.randomUUID().toString(),
                                    title = "Sleep Session",
                                    notes = "Recorded session",
                                    startTime = startTime.value!!,
                                    startZoneOffset = ZoneOffset.UTC,
                                    endTime = endTime,
                                    endZoneOffset = ZoneOffset.UTC,
                                    duration = Duration.between(startTime.value, endTime),
                                    stages = listOf() // Aggiungi stadi della sessione, se presenti
                                )
                                Log.d(TAG, "New session: $newSession")
                                sessionsList.toMutableList().add(newSession) // Aggiungi la nuova sessione alla lista
                                Log.d(TAG, "Sessions list: $sessionsList")
                                onInsertClick() // Salva la nuova sessione
                                isRecording.value = false // Aggiorna lo stato
                            } else {
                                // Avvia la registrazione
                                startTime.value = Instant.now()
                                isRecording.value = true
                            }
                        }
                    ) {
                        Text(if (isRecording.value) "Stop" else "Start")
                    }
                }

                // Mostra la lista delle sessioni
                items(sessionsList) { session ->
                    SleepSessionRow(session)
                }
            }
        }}
    }
}