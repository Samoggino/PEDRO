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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.ActivityType.CYCLING
import com.lam.pedro.data.activity.ActivityType.DRIVE
import com.lam.pedro.data.activity.ActivityType.LIFT
import com.lam.pedro.data.activity.ActivityType.LISTEN
import com.lam.pedro.data.activity.ActivityType.RUN
import com.lam.pedro.data.activity.ActivityType.SIT
import com.lam.pedro.data.activity.ActivityType.SLEEP
import com.lam.pedro.data.activity.ActivityType.TRAIN
import com.lam.pedro.data.activity.ActivityType.WALK
import com.lam.pedro.data.activity.ActivityType.YOGA
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.CyclingSession
import com.lam.pedro.data.activity.GenericActivity.RunSession
import com.lam.pedro.data.activity.GenericActivity.TrainSession
import com.lam.pedro.data.activity.GenericActivity.WalkSession
import com.lam.pedro.data.activity.toMonthNumber
import com.lam.pedro.placeholder
import com.lam.pedro.presentation.serialization.ViewModelRecordFactory
import com.lam.pedro.presentation.serialization.ViewModelRecords
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties

@Composable
fun ScreenCharts(
    activityType: ActivityType,
    viewModel: ViewModelRecords = viewModel(factory = ViewModelRecordFactory())
) {
    var selectedMetric by remember { mutableStateOf(LabelMetrics.Distance) }
    val activities by viewModel.activitySessions.observeAsState(emptyList())
    val error by viewModel.error.observeAsState("")

    LaunchedEffect(selectedMetric, error, activityType, activities) {
        Log.d("Supabase", "Caricamento delle attività di tipo $activityType")
        Log.d("Supabase", "Attività caricate: $activities")
        viewModel.loadActivitySession(activityType)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .placeholder(
                    isLoading = activities.isEmpty(),
                    showShimmerAnimation = true,
                    backgroundColor = activityType.color
                ),
            contentAlignment = Alignment.Center
        ) {
            if (activities.isEmpty()) {
                Text(text = "No data available", color = Color.White)

            } else {

                val barsList = generateBarsList(
                    activities = activities,
                    activityType = activityType,
                    selectedMetric = selectedMetric
                )

                Chart(barsList)
            }
        }

        MetricSelector(
            onMetricChange = { metric ->
                selectedMetric = LabelMetrics.valueOf(metric)
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

fun <T> List<T>.toMonthlyBarsList(
    getValue: (T) -> Double,
    label: LabelMetrics
): List<Bars> where T : GenericActivity {
    return this
        .groupBy { it.basicActivity.startTime.toMonthNumber() } // Raggruppiamo per mese
        .toList()
        .sortedBy { it.first }
        .map { (month, sessionsInMonth) ->
            Bars(
                label = month.toString(),
                values = listOf(
                    Bars.Data(
                        label = label.toString(),
                        value = sessionsInMonth.sumOf { session -> getValue(session) },
                        color = Brush.verticalGradient(
                            colors = listOf(
                                sessionsInMonth[0].activityType.color,
                                sessionsInMonth[0].activityType.color,
                            )
                        )
                    )
                )
            )
        }
}


fun generateBarsList(
    activities: List<GenericActivity>,
    activityType: ActivityType,
    selectedMetric: LabelMetrics
): List<Bars> {

    when {
        activityType.energyMetrics && activityType.distanceMetrics -> {

            when (activityType) {

                WALK -> {
                    val sessions = activities.map { it as WalkSession }
                    return sessions.toMonthlyBarsList(
                        getValue = {
                            metrics(
                                selectedMetric = selectedMetric,
                                distance = it.distance,
                                elevationGained = it.elevationGained,
                                totalCalories = it.totalEnergy,
                                activeCalories = it.activeEnergy,
                                duration = it.basicActivity.durationInMinutes()
                            )
                        },
                        label = selectedMetric
                    )
                }

                CYCLING -> {
                    val sessions = activities.map { it as CyclingSession }
                    return sessions.toMonthlyBarsList(
                        getValue = {
                            metrics(
                                selectedMetric = selectedMetric,
                                distance = it.distance,
                                elevationGained = it.elevationGained,
                                totalCalories = it.totalEnergy,
                                activeCalories = it.activeEnergy,
                                duration = it.basicActivity.durationInMinutes()
                            )
                        },
                        label = selectedMetric
                    )
                }

                RUN -> {
                    val sessions = activities.map { it as RunSession }
                    return sessions.toMonthlyBarsList(
                        getValue = {
                            metrics(
                                selectedMetric = selectedMetric,
                                distance = it.distance,
                                elevationGained = it.elevationGained,
                                totalCalories = it.totalEnergy,
                                activeCalories = it.activeEnergy,
                                duration = it.basicActivity.durationInMinutes()
                            )
                        },
                        label = selectedMetric
                    )
                }

                else -> return emptyList()
            }


        }

        activityType.energyMetrics -> {

            when (activityType) {
                TRAIN -> {
                    val sessions = activities.map { it as TrainSession }
                    return sessions.toMonthlyBarsList(
                        getValue = {
                            metrics(
                                selectedMetric = selectedMetric,
                                totalCalories = it.totalEnergy,
                                activeCalories = it.activeEnergy,
                                duration = it.basicActivity.durationInMinutes()
                            )
                        },
                        label = selectedMetric
                    )
                }

                YOGA -> {
                    val sessions = activities.map { it as GenericActivity.YogaSession }
                    return sessions.toMonthlyBarsList(
                        getValue = {
                            metrics(
                                selectedMetric = selectedMetric,
                                totalCalories = it.totalEnergy,
                                activeCalories = it.activeEnergy,
                                duration = it.basicActivity.durationInMinutes()
                            )
                        },
                        label = selectedMetric
                    )
                }

                LIFT -> {
                    val sessions = activities.map { it as GenericActivity.LiftSession }
                    return sessions.toMonthlyBarsList(
                        getValue = {
                            metrics(
                                selectedMetric = selectedMetric,
                                totalCalories = it.totalEnergy,
                                activeCalories = it.activeEnergy,
                                duration = it.basicActivity.durationInMinutes()
                            )
                        },
                        label = selectedMetric
                    )
                }

                else -> return emptyList()
            }

        }

        activityType.distanceMetrics -> when (activityType) {

            DRIVE -> {
                val sessions = activities.map { it as GenericActivity.DriveSession }
                return sessions.toMonthlyBarsList(
                    getValue = {
                        metrics(
                            selectedMetric = selectedMetric,
                            distance = it.distance,
                            elevationGained = it.elevationGained,
                            duration = it.basicActivity.durationInMinutes()
                        )
                    },
                    label = selectedMetric
                )
            }


            else -> return emptyList()
        }


        else -> return emptyList()
    }

}

private fun metrics(
    selectedMetric: LabelMetrics?,
    distance: Length? = null,
    elevationGained: Length? = null,
    totalCalories: Energy? = null,
    activeCalories: Energy? = null,
    duration: Double
) = when (selectedMetric) {
    LabelMetrics.Distance -> distance?.inMeters!!
    LabelMetrics.Elevation -> elevationGained?.inMeters!!
    LabelMetrics.TotalCalories -> totalCalories?.inKilocalories!!
    LabelMetrics.ActiveCalories -> activeCalories?.inKilocalories!!
    LabelMetrics.Duration -> duration
    else -> 0.0
}

@Composable
fun MetricSelector(
    onMetricChange: (String) -> Unit,
    activityType: ActivityType
) {

    DropdownMenu(
        expanded = true,
        onDismissRequest = { /* Handle dismiss */ },
        modifier = Modifier.fillMaxWidth()
    ) {

        when (activityType) {
            DRIVE -> {
                LabelMetrics.entries.filter { it != LabelMetrics.ActiveCalories }
                    .forEach { metric ->
                        DropdownMenuItem(
                            onClick = { onMetricChange(metric.toString()) },
                            text = { Text(text = metric.toString()) },
                        )
                    }
            }

            WALK, RUN, CYCLING -> {
                LabelMetrics.entries.forEach { metric ->
                    DropdownMenuItem(
                        onClick = { onMetricChange(metric.toString()) },
                        text = { Text(text = metric.toString()) },
                    )
                }
            }

            TRAIN, YOGA, LIFT -> {
                LabelMetrics.entries.filter { it != LabelMetrics.Distance }.forEach { metric ->
                    DropdownMenuItem(
                        onClick = { onMetricChange(metric.toString()) },
                        text = { Text(text = metric.toString()) },
                    )
                }

            }

            SIT, SLEEP, LISTEN -> {
                Text(text = "No metrics available")
            }
        }

    }
}

// enum delle label che voglio mostrare
enum class LabelMetrics {
    Distance,
    TotalCalories,
    Elevation,
    Duration,
    ActiveCalories,
}
