package com.lam.pedro.presentation.serialization.screen.sleepdata

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lam.pedro.data.SleepSessionData
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes@Composable
fun SleepSessionItem(sleepSession: SleepSessionData) {
    val (hours, minutes) = remember(sleepSession.duration) {
        Pair(sleepSession.duration?.hours, sleepSession.duration?.minutes)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(26.dp) // Bordi arrotondati
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Durata: ${hours ?: 0} ore e ${minutes ?: 0} minuti",
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
//            Text(
//                text = "Stadi: ${sleepSession.stages}",
//                style = MaterialTheme.typography.bodySmall
//            )
        }
    }
}
