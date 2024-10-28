package com.lam.pedro.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.navigation.NavController
import com.example.healthconnectsample.data.HealthConnectManager
import com.example.healthconnectsample.data.SleepSessionData
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadDataScreen(
    healthConnectManager: HealthConnectManager,
    navController: NavController,
    viewModel: ModelReadHealthConnect = ModelReadHealthConnect()
) {
    var sleepSessions by remember { mutableStateOf(emptyList<SleepSessionData>()) }
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    val end2 = ZonedDateTime.now()
    val end1 = end2.minusDays(1)
    val start1 = end1.minusHours(5)

    // Carica i dati di HealthConnect
    LaunchedEffect(Unit) {
        isRefreshing = true
        sleepSessions = viewModel.getSleepSessions()
        isRefreshing = false
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {
                sleepSessions = viewModel.getSleepSessions()
                isRefreshing = false
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B94F3)) // Colore di sfondo chiaro
    ) {
        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Titolo
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color(0xFF113E5C), shape = RoundedCornerShape(26.dp)) // Bordi arrotondati
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Sessioni di Sonno",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Pulsante per aggiungere una nuova sessione di sonno
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.uploadSleepSession(
                                    navController,
                                    healthConnectManager,
                                    SleepSessionData(
                                        uid = UUID.randomUUID().toString(),
                                        title = "New Sleep",
                                        notes = "Another good sleep",
                                        startTime = start1.toInstant(),
                                        startZoneOffset = start1.offset,
                                        endTime = end1.toInstant(),
                                        endZoneOffset = end1.offset,
                                        duration = Duration.between(start1, end1),
                                        stages = listOf(
                                            SleepSessionRecord.Stage(
                                                stage = SleepSessionRecord.STAGE_TYPE_DEEP,
                                                startTime = start1.toInstant(),
                                                endTime = end1.toInstant()
                                            )
                                        )
                                    )
                                )
                                sleepSessions = viewModel.getSleepSessions()
                            }
                        },
                        shape = RoundedCornerShape(26.dp) // Bordi arrotondati
                    ) {
                        Text("Aggiungi sessione di sonno")
                    }
                }
            }

            if (sleepSessions.isEmpty()) {
                item {
                    // Indicatore di caricamento se non ci sono sessioni di sonno
                    if (isRefreshing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            IndeterminateCircularIndicator()
                        }
                    } else {
                        Text(
                            text = "Nessuna sessione di sonno trovata",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(sleepSessions, key = { it.uid }) { sleepSession ->
                    SleepSessionItem(sleepSession)
                }
            }
        }
    }
}

@Composable
fun IndeterminateCircularIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

