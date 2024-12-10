package com.lam.pedro.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@Composable
fun NewActivityControlButtons(
    isPaused: Boolean,
    visible: Boolean,
    color: Color,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    density: Density
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        PlayPauseButton(
            isPaused = isPaused,
            visible = visible,
            color = color,
            onButtonClick = onPlayPauseClick
        )

        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally { with(density) { -40.dp.roundToPx() } } + fadeIn(),
            exit = slideOutHorizontally { with(density) { -40.dp.roundToPx() } } + fadeOut()
        ) {
            StopButton(
                onStopClick = onStopClick
            )
        }
    }
}
