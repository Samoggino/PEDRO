package com.lam.pedro.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.healthconnectsample.data.SleepSessionData
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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
            // Calcola le ore dalla durata
            val hours = sleepSession.duration?.hours
            val minutes = sleepSession.duration?.minutes

            Text(
                text = "Durata: $hours ore e $minutes minuti",
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
            Text(
                text = "Stadi: ${sleepSession.stages}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}