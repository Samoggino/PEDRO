package com.lam.pedro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun PlayPauseButton(
    isPaused: Boolean,
    visible: Boolean,
    color: Color,
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(26.dp))
            .size(140.dp)
            .background(color)
    ) {
        IconButton(
            onClick = onButtonClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = if (!isPaused) R.drawable.pause_icon else R.drawable.play_icon),
                contentDescription = if (visible) "Pause" else "Play",
                modifier = Modifier.size(60.dp)
            )
        }
    }
}
