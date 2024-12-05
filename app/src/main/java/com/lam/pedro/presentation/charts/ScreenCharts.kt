package com.lam.pedro.presentation.charts

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.ActivityType.CYCLING
import com.lam.pedro.data.activity.ActivityType.DRIVE
import com.lam.pedro.data.activity.ActivityType.LIFT
import com.lam.pedro.data.activity.ActivityType.RUN
import com.lam.pedro.data.activity.ActivityType.TRAIN
import com.lam.pedro.data.activity.ActivityType.WALK
import com.lam.pedro.data.activity.ActivityType.YOGA
import com.lam.pedro.placeholder
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties


@Composable
fun ScreenCharts(
    activityType: ActivityType,
    navController: NavController, // NavController per gestire la navigazione
    viewModelCharts: ViewModelCharts = viewModel(factory = ViewModelChartsFactory())
) {
    val barsList by viewModelCharts.barsList.observeAsState(emptyList())
    val error by viewModelCharts.error.observeAsState("")
    val viewModelIsLoading by viewModelCharts.isLoading.observeAsState(false)

    LaunchedEffect(activityType) {
        Log.d("Supabase", "Caricamento dati per $activityType")
        viewModelCharts.loadActivityData(activityType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (error.isNotEmpty()) {
            Text(text = "Errore: $error", color = Color.Red)
            return@Column
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(
                    isLoading = viewModelIsLoading,
                    showShimmerAnimation = true,
                    backgroundColor = activityType.color
                ),
            contentAlignment = Alignment.Center
        ) {
            if (barsList.isEmpty() && !viewModelIsLoading) {
                Text(text = "No data available", color = Color.White)
            } else {
                Chart(barsList)
            }
        }

        MetricSelector(
            onMetricChange = { metric ->
                viewModelCharts.changeMetric(LabelMetrics.valueOf(metric), activityType)
            },
            activityType = activityType
        )
    }
}


@Composable
fun Chart(barsList: List<Bars>) {

    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )

    val animationVelocity = AnimationMode.Together {
        it * 75L
    }

    ColumnChart(
        modifier = Modifier
            .height(300.dp)
            .padding(start = 22.dp, end = 22.dp, top = 10.dp, bottom = 20.dp),
        data = barsList,
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 10.dp,
            thickness = 15.dp
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = textStyle,
            padding = 10.dp
        ),
        animationMode = animationVelocity,
        popupProperties = PopupProperties(
            textStyle = textStyle,
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = textStyle,
            padding = 0.dp,
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = true,
            textStyle = textStyle,
        ),
    )

}


@Composable
fun MetricSelector(
    onMetricChange: (String) -> Unit,
    activityType: ActivityType
) {
    val availableMetrics = when (activityType) {
        DRIVE -> LabelMetrics.entries.filter { it != LabelMetrics.ActiveCalories }
        TRAIN, YOGA, LIFT -> LabelMetrics.entries.filter { it != LabelMetrics.Distance && it != LabelMetrics.Elevation }
        WALK, RUN, CYCLING -> LabelMetrics.entries
        else -> emptyList()
    }

    if (availableMetrics.isEmpty()) {
        Text(text = "No metrics available")
        return
    }

    DropdownMenu(
        expanded = true,
        onDismissRequest = { /* Handle dismiss */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        availableMetrics.forEach { metric ->
            DropdownMenuItem(
                onClick = { onMetricChange(metric.toString()) },
                text = { Text(text = metric.toString()) }
            )
        }
    }
}

