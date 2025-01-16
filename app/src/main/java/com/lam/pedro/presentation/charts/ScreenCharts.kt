package com.lam.pedro.presentation.charts

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.activitySupabase.ActivitySupabaseSupabaseRepositoryImpl


@Composable
fun ActivityChart(
    chartData: Map<String, Double>,
    modifier: Modifier = Modifier,
    activityColor: Color
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (chartData.isEmpty()) {
            Text(
                text = "No data available",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            BarChart(
                chartData = chartData,
                modifier = Modifier.fillMaxSize(),
                activityColor = activityColor
            )
        }
    }
}


@Composable
fun StaticActivityChart(
    metric: LabelMetrics,
    activities: List<GenericActivity>,
    modifier: Modifier = Modifier,
    timePeriod: TimePeriod,
    activityEnum: ActivityEnum
) {

    val viewModel: ViewModelCharts = viewModel(
        factory = ChartsViewModelFactory(
            activityRepository = ActivitySupabaseSupabaseRepositoryImpl()
        )
    )

    // Trasforma le attività in dati per il grafico
    val chartData = remember(activities, metric) {
        viewModel.buildBarsList(activities, metric, timePeriod)
    }

    ActivityChart(
        chartData = chartData,
        modifier = modifier,
        activityColor = activityEnum.color
    )
}

@Composable
fun FetchingActivityChart(
    activityEnum: ActivityEnum,
    metric: LabelMetrics,
    viewModelCharts: ViewModelCharts = viewModel(
        factory = ChartsViewModelFactory(
            activityRepository = ActivitySupabaseSupabaseRepositoryImpl()
        )
    ),
    modifier: Modifier = Modifier,
    timePeriod: TimePeriod
) {
    // Carica i dati al montaggio del Composable
    LaunchedEffect(activityEnum, metric) {
        Log.i("FetchingActivityChart", "Loading data for $activityEnum with metric $metric")
        viewModelCharts.loadActivityData(
            activityEnum = activityEnum,
            metric = metric,
            timePeriod = timePeriod
        )
    }

    // Osserva lo stato dei dati dal ViewModel
    val chartState by viewModelCharts.chartState.observeAsState(ChartState.Loading)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(8.dp)
    ) {
        when (chartState) {
            is ChartState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = activityEnum.color
                )
            }

            is ChartState.Success -> {
                val chartsData = (chartState as ChartState.Success).data
                ActivityChart(
                    chartData = chartsData,
                    modifier = Modifier.fillMaxSize(),
                    activityColor = activityEnum.color
                )
            }

            is ChartState.Error -> {
                val error = (chartState as ChartState.Error).message
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
