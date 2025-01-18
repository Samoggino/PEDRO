package com.lam.pedro.presentation.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties

@Composable
fun BarChart(
    activityColor: Color,
    chartData: Map<String, Double>, // Mappa delle etichette e dei valori
    modifier: Modifier = Modifier
        .height(300.dp)
        .padding(15.dp),
    metric: LabelMetrics
) {
    val textStyle = remember {
        TextStyle(
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
        )
    }

    ColumnChart(
        modifier = modifier,
        data = chartData.map { (month, value) -> // Crea la lista di Bars dalla mappa
            Bars(
                label = month,
                values = listOf(
                    Bars.Data(
                        label = metric.name,
                        value = value,
                        color = SolidColor(activityColor) // Colore statico o dinamico
                    )
                )
            )
        },
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
            padding = 10.dp,
            rotation = LabelProperties.Rotation(degree = 0f),
            labels = chartData.keys.toList(), // Usa le chiavi della mappa come etichette
        ),
        popupProperties = PopupProperties(
            textStyle = textStyle,
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            textStyle = textStyle,
            padding = 10.dp,
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = false,
            textStyle = textStyle,
        ),
    )
}

