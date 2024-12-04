package com.lam.pedro.presentation.screen.activities.startactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/*
@Composable
fun StartActivityContent(
    color: Color,
    isPaused: Boolean,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    visible: Boolean,
    showDialog: Boolean,
    onDialogDismiss: () -> Unit,
    onDialogConfirm: (String, String) -> Unit,
    title: String,
    notes: String,
    onTitleChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Pulsanti principali
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PlayPauseButton(color, isPaused, onPlayPauseClick)
            Spacer(modifier = Modifier.width(20.dp))
            StopButton(visible, onStopClick)
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Dialogo
        if (showDialog) {
            StartActivityDialog(
                title = title,
                notes = notes,
                onTitleChange = onTitleChange,
                onNotesChange = onNotesChange,
                onDismiss = onDialogDismiss,
                onConfirm = onDialogConfirm
            )
        }
    }
}

 */
