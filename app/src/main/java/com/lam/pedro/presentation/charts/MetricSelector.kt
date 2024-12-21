package com.lam.pedro.presentation.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lam.pedro.data.activity.ActivityEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricSelector(
    onMetricChange: (LabelMetrics) -> Unit,
    activityEnum: ActivityEnum
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedMetric by remember {
        mutableStateOf(
            getAvailableMetricsForActivity(activityEnum).firstOrNull() ?: LabelMetrics.DURATION
        )
    }

    val availableMetrics = getAvailableMetricsForActivity(activityEnum)

    when {
        availableMetrics.isEmpty() -> {
            Text(text = "No metrics available")
            return
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = selectedMetric.toString(),
            onValueChange = { },
            label = { Text("Metric") }, // Add a label for accessibility
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth()
                .padding(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableMetrics.forEach { metric ->
                DropdownMenuItem(
                    text = { Text(metric.toString()) },
                    onClick = {
                        selectedMetric = metric
                        onMetricChange(metric)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
