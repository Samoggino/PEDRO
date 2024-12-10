package com.lam.pedro.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerStatsDisplay(
    elapsedTime: Int,
    steps: Float,
    averageSpeed: Double,
    distance: MutableState<Double>,
    color: Color
) {
    val minutes = (elapsedTime / 60000) % 60
    val seconds = (elapsedTime / 1000) % 60
    val centiseconds = (elapsedTime % 1000) / 10

    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format("%02d:%02d:%02d", minutes, seconds, centiseconds),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 55.sp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        StatsRow(
            steps = steps,
            speed = averageSpeed,
            distance = distance,
            color = color,
            modifier = Modifier.weight(1f)
        )
    }
}
