package com.lam.pedro.presentation.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.placeholder
import com.lam.pedro.presentation.serialization.MetricSelector
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCharts(
    activityType: ActivityType,
    viewModelCharts: ViewModelCharts = viewModel(factory = ViewModelChartsFactory())
) {
    val barsList by viewModelCharts.barsList.observeAsState(emptyList())
    val error by viewModelCharts.error.observeAsState("")
    val viewModelIsLoading by viewModelCharts.isLoading.observeAsState(false)

    LaunchedEffect(activityType) {
        viewModelCharts.loadActivityData(activityType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = activityType.name) },
            )
        },
    ) {
        if (error.isNotEmpty()) {
            Text(text = "Errore: $error", color = Color.Red)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), // Aggiunge il padding per tenere conto dell'area della top bar
            contentAlignment = Alignment.Center // Centra il contenuto orizzontalmente e verticalmente
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
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
                        if (barsList.isNotEmpty())
                            Chart(barsList)
                    }
                }
                MetricSelector(
                    onMetricChange = { metric ->
                        viewModelCharts.changeMetric(LabelMetrics.valueOf(metric))
                    },
                    activityType = activityType
                )
            }
        }
    }
}

@Composable
fun Chart(barsList: List<Bars>) {

    val textStyle =
        TextStyle(
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

    val animationVelocity = AnimationMode.Together {
        it * 75L
    }

    ColumnChart(
        modifier = Modifier
            .height(300.dp)
            .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
        data = barsList,
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
            labels = listOf(
                "Jan",
                "Feb",
                "Mar",
                "Apr",
                "May",
                "Jun",
                "Jul",
                "Aug",
                "Sep",
                "Oct",
                "Nov",
                "Dec"
            ),
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
    )

}