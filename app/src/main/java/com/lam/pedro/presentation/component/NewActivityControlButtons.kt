package com.lam.pedro.presentation.component

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.R

@Composable
fun NewActivityControlButtons(
    visible: Boolean,
    isPaused: Boolean,
    showDialog: Boolean,
    isStopAction: Boolean,
    color: Color,
    requestLocationPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit
) {
    /*
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .size(130.dp)
                .background(color)
        ) {
            IconButton(
                onClick = {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    onPlayPauseClick()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = if (!isPaused) R.drawable.pause_icon else R.drawable.play_icon),
                    contentDescription = if (visible) "Pause" else "Play",
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally { with(LocalDensity.current) { -40.dp.roundToPx() } } + fadeIn(),
            exit = slideOutHorizontally { with(LocalDensity.current) { -40.dp.roundToPx() } } + fadeOut()
        ) {
            IconButton(
                onClick = {
                    onStopClick()
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .size(130.dp)
                    .background(Color(0xFFF44336))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.stop_icon),
                    contentDescription = "Stop",
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }

     */
}
