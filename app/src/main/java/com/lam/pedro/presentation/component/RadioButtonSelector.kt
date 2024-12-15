package com.lam.pedro.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtonSelector(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionChange: (String) -> Unit,
    color: Color
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)

        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionChange(option) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = color
                    )
                )
                Text(option, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
