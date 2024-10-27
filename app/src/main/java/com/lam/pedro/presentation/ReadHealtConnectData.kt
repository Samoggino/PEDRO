package com.lam.pedro.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    // Stato per memorizzare le sessioni di sonno lette
    var sleepSessions by remember { mutableStateOf(emptyList<SleepSessionData>()) }
    val coroutineScope = rememberCoroutineScope()

    val end2 = ZonedDateTime.now()
    val end1 = end2.minusDays(1)
    val start1 = end1.minusHours(5)

    // Carica i dati di HealthConnect
    LaunchedEffect(Unit) {

        sleepSessions = viewModel.getSleepSessions()

        Log.i("HealthConnect", "Dati delle sessioni di sonno caricati in ReadDataScreen")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Text(
                text = "Sessioni di Sonno",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )


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
                        sleepSessions = viewModel.getSleepSessions().toList()
                    }
                }
            ) {
                Text("Aggiungi sessione di sonno")
            }


        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sleepSessions) { sleepSession ->
                SleepSessionItem(sleepSession)
            }
        }
    }
}
