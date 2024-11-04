package com.lam.pedro.presentation.serialization.screen.exercisedata

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.healthconnectsample.data.HealthConnectManager
import com.lam.pedro.data.ExerciseSessionData
import com.lam.pedro.presentation.serialization.viewmodel.exercisedata.ViewModelExerciseData
import com.lam.pedro.presentation.serialization.viewmodel.exercisedata.ViewModelExerciseDataFactory
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    healthConnectManager: HealthConnectManager,
    navController: NavController,
) {

    var exerciseSession by remember { mutableStateOf(emptyList<ExerciseSessionData>()) }
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    val context = LocalContext.current
    val viewModel: ViewModelExerciseData = viewModel(factory = ViewModelExerciseDataFactory())

    val end2 = ZonedDateTime.now()
    val end1 = end2.minusDays(1)
    val start1 = end1.minusHours(5)

    // Carica i dati di HealthConnect
    LaunchedEffect(Unit) {
        isRefreshing = true
        exerciseSession = viewModel.getExerciseSessions()
        isRefreshing = false
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            Log.i("Supabase-HealthConnect", "Aggiornamento dati ExerciseDataScreen")
            isRefreshing = true
            coroutineScope.launch {
                exerciseSession = viewModel.getExerciseSessions()
                isRefreshing = false
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF033766)) // Colore di sfondo chiaro
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
                        .background(
                            Color(0xFF113E5C),
                            shape = RoundedCornerShape(26.dp)
                        ) // Bordi arrotondati
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Sessioni di Esercizio",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Pulsante per aggiungere una nuova sessione di esercizio
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.uploadExerciseSession(
                                    navController,
                                    ExerciseSessionData(
                                        uid = UUID.randomUUID().toString(),
                                        totalActiveTime = Duration.between(
                                            start1,
                                            end1
                                        ), // Esempio: 90 minuti di attività
                                        totalSteps = 12500,
                                        totalDistance = Length.kilometers(10.3), // Distanza percorsa di 8 km
                                        totalEnergyBurned = Energy.calories(100.1), // Energia bruciata in kcal
                                        minHeartRate = 60, // Frequenza cardiaca minima di 60 bpm
                                        maxHeartRate = 145, // Frequenza cardiaca massima di 145 bpm
                                        avgHeartRate = 115 // Frequenza cardiaca media di 115 bpm
                                    ),
                                    context = context
                                )
                                exerciseSession = viewModel.getExerciseSessions()
                            }
                        },
                        shape = RoundedCornerShape(26.dp) // Bordi arrotondati
                    ) {
                        Text("Aggiungi sessione di esercizio")
                    }
                }
            }

            if (exerciseSession.isEmpty()) {
                item {
                    // Indicatore di caricamento se non ci sono sessioni di esercizio
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
                            text = "Nessuna sessione di esercizio trovata",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(exerciseSession, key = { it.uid }) { exerciseSession ->
                    ExerciseDataItem(exerciseSession)
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

