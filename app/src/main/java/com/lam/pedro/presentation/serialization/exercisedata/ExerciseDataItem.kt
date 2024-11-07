package com.lam.pedro.presentation.serialization.exercisedata

import android.util.Log
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
import com.lam.pedro.data.activity.ExerciseSessionData

@Composable
fun ExerciseDataItem(exerciseSession: ExerciseSessionData) {

    Log.i("ExerciseDataItem", "ExerciseDataItem $exerciseSession")

    val (hours, minutes) = remember(exerciseSession.totalActiveTime) {
        Pair(
            exerciseSession.totalActiveTime?.toHours(),
            exerciseSession.totalActiveTime?.toMinutes()
        )
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
                text = "Durata: $hours ore e $minutes minuti",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Passi: ${exerciseSession.totalSteps}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Distanza: ${exerciseSession.totalDistance} metri",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Calorie bruciate: ${exerciseSession.totalEnergyBurned} kcal",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Frequenza cardiaca minima: ${exerciseSession.minHeartRate}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Frequenza cardiaca massima: ${exerciseSession.maxHeartRate}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Frequenza cardiaca media: ${exerciseSession.avgHeartRate}",
                style = MaterialTheme.typography.bodySmall
            )

        }
    }
}
