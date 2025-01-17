package com.lam.pedro.presentation.component

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    accentColor: Color = MaterialTheme.colorScheme.primary // Colore di default
) {
    val datePickerState = rememberDatePickerState()

    // Imposta un MaterialTheme personalizzato per il DatePicker
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = accentColor, // Colore principale (data selezionata, pulsanti, ecc.)
            onPrimary = Color.White // Colore del testo su sfondo primario
        )
    ) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = accentColor) // Colore dei pulsanti
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = accentColor) // Colore dei pulsanti
                ) {
                    Text("Cancel")
                }
            }
        ) {
            // Personalizzazione del DatePicker
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContentColor = Color.White, // Colore del testo per il giorno selezionato
                    selectedDayContainerColor = accentColor, // Colore di sfondo per il giorno selezionato
                    todayContentColor = accentColor, // Colore del testo per il giorno corrente
                    todayDateBorderColor = accentColor // Colore del bordo per il giorno corrente
                )
            )
        }
    }
}

