package com.lam.pedro.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RequestNotificationPermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notification permission") },
        text = { Text("Pedro needs to send notification to show you when it's recording your activity") },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text("Allow")
            }
        },
    )
}
