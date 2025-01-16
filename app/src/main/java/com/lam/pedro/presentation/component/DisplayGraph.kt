package com.lam.pedro.presentation.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.presentation.charts.LabelMetrics
import com.lam.pedro.presentation.charts.StaticActivityChart
import com.lam.pedro.presentation.charts.ViewModelCharts
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue


@Composable
fun DisplayGraph(
    items: List<GenericActivity>,
    availableMetrics: List<LabelMetrics>,
    timePeriod: ViewModelCharts.TimePeriod
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { availableMetrics.size })

    // Scorrimento automatico delle pagine
    LaunchedEffect(pagerState, timePeriod) {
        while (true) {
            delay(6000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 30.dp)
        ) { page ->

            val currentMetric = availableMetrics[page]
            val pageOffset = (page - pagerState.currentPage) + pagerState.currentPageOffsetFraction
            val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(-1f, 1f).absoluteValue)
            val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(-1f, 1f).absoluteValue)

            Box(
                modifier = Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
                    .fillMaxWidth()
                    .height(200.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = false
                    )
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Metric: ${currentMetric.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        StaticActivityChart(
                            metric = currentMetric,
                            activities = items,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            timePeriod = timePeriod
                        )
                    }
                }
            }
        }
    }
}
