package com.lam.pedro.presentation.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.placeholder
import com.lam.pedro.presentation.serialization.MetricSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCharts(
    activityType: ActivityType,
    viewModelCharts: ViewModelCharts,
    navController: NavController
) {
    val chartState by viewModelCharts.chartState.observeAsState(ChartState.Loading)

    LaunchedEffect(activityType) {
        viewModelCharts.loadActivityData(activityType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = activityType.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            ChartContent(
                chartState = chartState,
                activityType = activityType
            )

            MetricSelector(
                onMetricChange = { metric ->
                    viewModelCharts.changeMetric(LabelMetrics.valueOf(metric))
                },
                activityType = activityType
            )
        }
    }
}

@Composable
fun ChartContent(
    activityType: ActivityType,
    chartState: ChartState,
    modifier: Modifier =
        Modifier
            .fillMaxWidth()
            .height(300.dp)
            .placeholder(
                isLoading = chartState is ChartState.Loading,
                showShimmerAnimation = true,
                backgroundColor = activityType.color
            )
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (chartState) {
            is ChartState.Loading -> {
                // do nothing. Placeholder is already shown
            }

            is ChartState.Success -> {
                if (chartState.data.isEmpty()) {
                    Text("No data available", color = Color.White)
                } else {
                    BarChart(chartState.data)
                }
            }

            is ChartState.Error -> Text("Error: ${chartState.error}", color = Color.Red)
        }
    }
}
