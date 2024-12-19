package com.lam.pedro.presentation.charts

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import kotlinx.datetime.Month

@Composable
fun BarChart(
    chartData: List<Bars>,
    modifier: Modifier = Modifier
        .height(300.dp)
        .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 20.dp)
) {

    val monthLabels = remember {
        (1..12).map { month ->
            Month(month).name.substring(0, 3) // Get first 3 letters of month name
        }
    }

    val textStyle = remember {
        TextStyle(
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }

    val animationVelocity = AnimationMode.Together {
        it * 75L
    }

    ColumnChart(
        modifier = modifier,
        data = chartData,
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 10.dp,
            thickness = 15.dp,
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = textStyle,
            padding = 10.dp, // spazio tra le etichette e il grafico
            rotation = LabelProperties.Rotation(degree = 0f),
            labels = monthLabels,
        ),
        animationMode = animationVelocity,
        popupProperties = PopupProperties(
            textStyle = textStyle,
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = textStyle,
            padding = 10.dp,
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = true,
            textStyle = textStyle,
        ),
        onBarLongClick = { barData ->
            Log.d("Charts", "Bar clicked: $barData")

        }
    )

}
