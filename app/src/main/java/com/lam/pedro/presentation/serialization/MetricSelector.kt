package com.lam.pedro.presentation.serialization

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.presentation.charts.availableMetricsFilter

@Composable
fun MetricSelector(
    onMetricChange: (String) -> Unit,
    activityType: ActivityType
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMetric by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        Log.d("Charts", "MetricSelector avviato")
    }

    val availableMetrics = availableMetricsFilter(activityType)

    if (availableMetrics.isEmpty()) {
        Text(text = "No metrics available")
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (expanded) {
                            expanded = false
                        }
                    }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = selectedMetric.ifEmpty { "Select Metric" },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp)
                    .background(activityType.color, shape = RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        activityType.color.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 16.dp)
                    .clickable { expanded = !expanded }
            )
        }

        AnimatedVisibility(visible = expanded) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(activityType.color, shape = RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        activityType.color.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            ) {
                items(availableMetrics) { metric ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedMetric = metric.toString()
                                onMetricChange(selectedMetric)
                                expanded = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = metric.toString(),
                        )
                    }
                }
            }
        }
    }
}