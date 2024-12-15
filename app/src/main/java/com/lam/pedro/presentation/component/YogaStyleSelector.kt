package com.lam.pedro.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/*
@Composable
fun YogaStyleSelector(
    yogaStyle: String,  // Passato dallo stato esterno
    onYogaStyleChange: (String) -> Unit, // Funzione per aggiornare lo stato esterno
    color: Color
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select yoga style:", style = MaterialTheme.typography.headlineSmall)

        // RadioButton per selezionare lo stile
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = yogaStyle == "Yin (gentle)",
                onClick = { onYogaStyleChange("Yin (gentle)") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = color
                )
            )
            Text("Yin (gentle)", modifier = Modifier.padding(start = 8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = yogaStyle == "Hatha (moderate)",
                onClick = { onYogaStyleChange("Hatha (moderate)") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = color
                )
            )
            Text("Hatha (moderate)", modifier = Modifier.padding(start = 8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = yogaStyle == "Vinyasa (vigorous)",
                onClick = { onYogaStyleChange("Vinyasa (vigorous)") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = color
                )
            )
            Text("Vinyasa (vigorous)", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

 */

@Composable
fun YogaStyleSelector(
    yogaStyle: String,
    onYogaStyleChange: (String) -> Unit,
    color: Color
) {
    RadioButtonSelector(
        title = "Select yoga style:",
        options = listOf("Yin (gentle)", "Hatha (moderate)", "Vinyasa (vigorous)"),
        selectedOption = yogaStyle,
        onOptionChange = onYogaStyleChange,
        color = color
    )
}
