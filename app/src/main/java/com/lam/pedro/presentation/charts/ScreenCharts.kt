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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.CyclingSession
import com.lam.pedro.data.activity.GenericActivity.RunSession
import com.lam.pedro.data.activity.GenericActivity.WalkSession
import com.lam.pedro.data.activity.toMonthNumber
import com.lam.pedro.presentation.serialization.ViewModelRecordFactory
import com.lam.pedro.presentation.serialization.ViewModelRecords
import com.lam.pedro.presentation.theme.PedroDarkGray
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
    val activities by viewModel.activitySessions.observeAsState(emptyList())
    val error by viewModel.error.observeAsState("")
    var selectedMetric by remember { mutableStateOf("Distance") }

    LaunchedEffect(true, error, activityType) {
        Log.d("Supabase", "Caricamento delle attività di tipo $activityType")
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
            val barsList = generateBarsList(activities, activityType, selectedMetric)
            Chart(barsList)
        }

        MetricSelector { metric ->
            selectedMetric = metric
        }
    }


}

fun generateBarsList(
    activities: List<Any>,
    activityType: ActivityType,
    selectedMetric: String
): List<Bars> {
    return when (activityType) {
        ActivityType.CYCLING -> {
            val sessions = activities.map { it as CyclingSession }
            sessions.toMonthlyBarsList {
                when (selectedMetric) {
                    "Distance" -> it.distance.inMeters
                    "Calories" -> it.totalEnergy.inKilocalories
                    "Elevation" -> it.elevationGained.inMeters
                    else -> 0.0
                }
            }
        }

        ActivityType.RUN -> {
            val sessions = activities.map { it as RunSession }
            sessions.toMonthlyBarsList { it.distance.inMeters }
        }

        ActivityType.WALK -> {
            val sessions = activities.map { it as WalkSession }
            sessions.toMonthlyBarsList { it.distance.inMeters }
        }

        else -> emptyList()
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

fun <T> List<T>.toMonthlyBarsList(getValue: (T) -> Double): List<Bars> where T : GenericActivity {
    return this
        .groupBy { it.basicActivity.startTime.toMonthNumber() } // Raggruppiamo per mese
        .toList()
        .sortedBy { it.first }
        .map { (month, sessionsInMonth) ->
            Bars(
                label = month.toString(),
                values = listOf(
                    Bars.Data(
                        label = sessionsInMonth[0].activityType.name,
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


fun Modifier.placeholder(
    isLoading: Boolean,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = RoundedCornerShape(15.dp),
    showShimmerAnimation: Boolean = true
): Modifier = composed {
    val highlight = if (showShimmerAnimation) {
        PlaceholderHighlight.shimmer()
    } else {
        null
    }
    val specifiedBackgroundColor = backgroundColor.takeOrElse { PedroDarkGray.copy(0.6f) }
    Modifier.placeholder(
        color = specifiedBackgroundColor,
        visible = isLoading,
        shape = shape,
        highlight = highlight
    )
}

@Composable
fun MetricSelector(
    onMetricChange: (String) -> Unit
) {
    val metrics = listOf("Distance", "Calories", "Elevation")

    DropdownMenu(
        expanded = true,
        onDismissRequest = { /* Handle dismiss */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        metrics.forEach { metric ->
            DropdownMenuItem(
                onClick = { onMetricChange(metric) },
                text = { Text(text = metric) },
            )
        }
    }
}
