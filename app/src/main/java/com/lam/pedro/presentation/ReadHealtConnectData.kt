package com.lam.pedro.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.navigation.NavController
import com.example.healthconnectsample.data.HealthConnectManager
import com.example.healthconnectsample.data.SleepSessionData
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.random.Random


@Composable
fun ReadDataScreen(
    healthConnectManager: HealthConnectManager,
    navController: NavController,
    viewModel: ModelReadHealthConnect = ModelReadHealthConnect()
) {
    // Stato per memorizzare le sessioni di sonno lette
    var sleepSessions by remember { mutableStateOf(emptyList<SleepSessionData>()) }

    val end2 = ZonedDateTime.now()
    val start2 = end2.minusHours(5)
    val end1 = end2.minusDays(1)
    val start1 = end1.minusHours(5)

    // Conversione dei dati in SleepSessionData serializzabile
    val sleepSessionData = SleepSessionData(
        uid = "1",
        title = "My sleep",
        notes = "Slept well",
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

    // Carica i dati di HealthConnect
    LaunchedEffect(Unit) {
        viewModel.uploadSleepSession(navController, healthConnectManager, sleepSessionData)

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
        Text(
            text = "Sessioni di Sonno",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sleepSessions) { sleepSession ->
                SleepSessionItem(sleepSession)
            }
        }
    }
}

@Composable
fun SleepSessionItem(sleepSession: SleepSessionData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Durata: ${sleepSession.duration} ore",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Note: ${sleepSession.notes ?: "Nessuna"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Data: ${sleepSession.startTime}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Finisce: ${sleepSession.endTime}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


