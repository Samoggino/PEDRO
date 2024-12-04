package com.lam.pedro.presentation.screen.activities.startactivity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.R

/*
@Composable
fun PlayPauseButton(color: Color, isPaused: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .size(150.dp)
            .background(color)
    ) {
        IconButton(onClick = onClick) {
            Image(
                painter = painterResource(id = if (isPaused) R.drawable.play_icon else R.drawable.pause_icon),
                contentDescription = if (isPaused) "Play" else "Pause",
                modifier = Modifier.size(75.dp)
            )
        }
    }
}

@Composable
fun StopButton(visible: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(visible = visible) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .size(150.dp)
                .background(Color(0xFFF44336))
        ) {
            Image(
                painter = painterResource(id = R.drawable.stop_icon),
                contentDescription = "Stop",
                modifier = Modifier.size(75.dp)
            )
        }
    }
}

 */
