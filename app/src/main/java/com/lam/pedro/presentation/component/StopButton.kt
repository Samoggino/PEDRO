package com.lam.pedro.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
fun StopButton(
    onStopClick: () -> Unit
) {
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        IconButton(
            onClick = onStopClick,
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .size(140.dp)
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

