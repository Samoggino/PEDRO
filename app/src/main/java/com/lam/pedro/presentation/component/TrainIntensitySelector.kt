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
fun TrainIntensitySelector(
    trainIntensity: String,  // Passato dallo stato esterno
    onYogaStyleChange: (String) -> Unit, // Funzione per aggiornare lo stato esterno
    color: Color
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select intensity:", style = MaterialTheme.typography.headlineSmall)

        // RadioButton per selezionare lo stile
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = trainIntensity == "gentle",
                onClick = { onYogaStyleChange("gentle") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = color
                )
            )
            Text("Gentle", modifier = Modifier.padding(start = 8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = trainIntensity == "moderate",
                onClick = { onYogaStyleChange("moderate") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = color
                )
            )
            Text("Moderate", modifier = Modifier.padding(start = 8.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = trainIntensity == "vigorous",
                onClick = { onYogaStyleChange("vigorous") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = color
                )
            )
            Text("Vigorous", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

 */



@Composable
fun TrainIntensitySelector(
    trainIntensity: String,
    onTrainIntensityChange: (String) -> Unit,
    color: Color
) {
    RadioButtonSelector(
        title = "Select intensity:",
        options = listOf("gentle", "moderate", "vigorous"),
        selectedOption = trainIntensity,
        onOptionChange = onTrainIntensityChange,
        color = color
    )
}