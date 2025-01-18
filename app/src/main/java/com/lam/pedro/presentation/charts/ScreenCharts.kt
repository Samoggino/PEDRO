package com.lam.pedro.presentation.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity


@Composable
fun ActivityChart(
    chartData: Map<String, Double>,
    activityColor: Color,
    metric: LabelMetrics
) {
    if (chartData.isEmpty()) {
        val textStyle = remember {
            TextStyle(
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
            )
        }

        Text(
            text = "No data available",
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            style = textStyle
        )

        return
    } else {
        BarChart(
            chartData = chartData,
            activityColor = activityColor,
            metric = metric,
        )
    }
}


@Composable
fun StaticActivityChart(
    metric: LabelMetrics,
    activities: List<GenericActivity>,
    timePeriod: TimePeriod,
    activityEnum: ActivityEnum
) {

    val viewModel: ViewModelCharts = viewModel(factory = ChartsViewModelFactory())

    // Trasforma le attività in dati per il grafico
    val chartData = remember(activities, metric) {
        viewModel.buildBarsList(activities, metric, timePeriod)
    }

    ActivityChart(
        chartData = chartData,
        activityColor = activityEnum.color,
        metric = metric,
    )
}