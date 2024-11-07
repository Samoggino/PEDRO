package com.lam.pedro.presentation.component

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.lam.pedro.data.CarouselItem
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun DisplayGraph(items: List<CarouselItem>) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { items.size })


    // Aggiungi un LaunchedEffect per gestire lo scorrimento automatico
    LaunchedEffect(pagerState) {
        while (true) {
            delay(6000) // Imposta l'intervallo di tempo tra una pagina e l'altra (es. 3000ms)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(
                    durationMillis = 700, // Durata dell'animazione in millisecondi
                    easing = FastOutSlowInEasing
                )
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
            items[page]


            val pageOffset =
                (page - pagerState.currentPage) + pagerState.currentPageOffsetFraction
            val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(-1f, 1f).absoluteValue)
            val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(-1f, 1f).absoluteValue)

            Box(
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = alpha
                    )
                    .fillMaxWidth()
                    .height(200.dp)
                    .shadow(
                        elevation = 8.dp, // Regola l'altezza dell'ombra
                        shape = RoundedCornerShape(16.dp), // Usa lo stesso RoundedCornerShape del clip
                        clip = false // Disabilita il clipping dell'ombra per dare un aspetto rialzato
                    )
            ) {

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(26.dp))
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("HERE GOES THE GRAPH", modifier = Modifier.align(Alignment.Center))
                }


            }
        }
    }

}