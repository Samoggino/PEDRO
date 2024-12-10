package com.lam.pedro.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NewActivitySaveAlertDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    isStopAction: Boolean,
    visible: Boolean,
    color: Color,
    activityTitle: String,
    onTitleChange: (String) -> Unit,
    isTitleEmpty: Boolean,
    notes: String,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Confirm", color = color) },
            text = {
                Column {
                    Text(
                        if (isStopAction) "Want to stop the activity? (you can change these while stopping)"
                        else if (visible) "Want to pause?"
                        else "Want to start?"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = activityTitle,
                        onValueChange = onTitleChange,
                        label = { Text("Title") },
                        isError = isTitleEmpty,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = color,
                            cursorColor = color,
                            focusedLabelColor = color,
                        )
                    )

                    if (isTitleEmpty) {
                        Text(
                            text = "Title is required",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        label = { Text("Note (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = color,
                            cursorColor = color,
                            focusedLabelColor = color,
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = "Yes", color = color)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Dismiss", color = color)
                }
            }
        )
    }
}
