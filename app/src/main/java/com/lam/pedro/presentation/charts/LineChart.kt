package com.lam.pedro.presentation.charts

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun MyLineChart(
    activities: List<GenericActivity>,
    activityEnum: ActivityEnum
) {

    val data = activities.map {
        it.basicActivity.startTime.atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    val textStyle = remember {
        TextStyle(
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
        )
    }

    LineChart(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(15.dp),
        data = remember { buildLines(activities, activityEnum) },
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = textStyle,
            padding = 10.dp,
            rotation = LabelProperties.Rotation(degree = 0f),
            labels = data, // Usa le chiavi della mappa come etichette
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = textStyle,
            padding = 10.dp,
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = false,
            textStyle = textStyle,
        )
    )
}

fun buildLines(activities: List<GenericActivity>, activityEnum: ActivityEnum): List<Line> {
    return activities.map { activity ->
        Line(
            label = activity.basicActivity.startTime.atZone(ZoneId.systemDefault())
                .format(dateFormatter),
            values = listOf(activity.basicActivity.durationInMinutes()),
            color = SolidColor(activityEnum.color),
            firstGradientFillColor = activityEnum.color.copy(alpha = .5f),
            secondGradientFillColor = activityEnum.color.copy(alpha = .05f),
            strokeAnimationSpec = tween(250, easing = EaseInOutCubic),
            gradientAnimationDelay = 10,
            drawStyle = DrawStyle.Stroke(width = 2.dp),
        )
    }
}