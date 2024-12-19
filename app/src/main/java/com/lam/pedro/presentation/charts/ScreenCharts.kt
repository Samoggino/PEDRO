package com.lam.pedro.presentation.charts

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.placeholder
import com.lam.pedro.presentation.serialization.MetricSelector
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCharts(
    activityType: ActivityType,
    viewModelCharts: ViewModelCharts,
    navController: NavController
) {
    val chartState by viewModelCharts.chartState.observeAsState(ChartState.Loading)

    LaunchedEffect(activityType) {
        Log.i("Charts", "ScreenCharts for $activityType")
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
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            onRefresh = {
                viewModelCharts.loadActivityData(activityType)
            },
            isRefreshing = chartState is ChartState.Loading
        ) {


            Column {
                MetricSelector(
                    onMetricChange = { metric ->
                        viewModelCharts.changeMetric(metric)
                    },
                    activityType = activityType
                )
                ChartContent(chartState = chartState, activityType = activityType)
            }
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
            .height(600.dp)
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

                val chartsData = chartState.data

                if (chartsData.isEmpty()) {
                    Text("No data available", color = Color.White)
                } else {
                    var showFirstChart by remember { mutableStateOf(false) }
                    var showSecondChart by remember { mutableStateOf(false) }

                    LazyColumn {
                        item {
                            AnimatedVisibility(
                                visible = showFirstChart,
                                enter = fadeIn() + scaleIn(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ),
                                exit = fadeOut() + scaleOut()
                            ) {
                                BarChart(chartsData)
                            }
                        }
                        item {
                            AnimatedVisibility(
                                visible = showSecondChart,
                                enter = fadeIn() + scaleIn(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ),
                                exit = fadeOut() + scaleOut()
                            ) {
                                MyRowChart(chartsData)
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        showFirstChart = true // Show the first chart immediately
                        delay(500) // Example delay before showing the second chart
                        showSecondChart = true
                    }
                }
            }

            is ChartState.Error -> Text("Error: ${chartState.error}", color = Color.Red)
        }
    }
}
