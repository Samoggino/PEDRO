package com.lam.pedro.presentation.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricSelector(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit,
    activityColor: Color = Color.Gray
) {
    var dropdownVisible by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = dropdownVisible,
        onExpandedChange = { dropdownVisible = !dropdownVisible },
    ) {
        // Mostra il periodo selezionato come TextField
        TextField(
            readOnly = true,
            value = selectedPeriod.name,
            onValueChange = { },
            label = { Text("Period") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownVisible) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedIndicatorColor = activityColor,
                unfocusedIndicatorColor = activityColor,
                focusedTrailingIconColor = activityColor,
                unfocusedTrailingIconColor = activityColor,
                focusedLabelColor = activityColor,
                unfocusedLabelColor = activityColor,
                cursorColor = activityColor
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(8.dp)
                .then(Modifier.exposedDropdownSize())
                .clip(RoundedCornerShape(10.dp))
        )

        // Menu a discesa con le opzioni del periodo
        ExposedDropdownMenu(
            expanded = dropdownVisible,
            onDismissRequest = { dropdownVisible = false }
        ) {
            TimePeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.name) },
                    onClick = {
                        onPeriodSelected(period) // Seleziona il periodo
                        dropdownVisible = false // Chiudi il menu dopo la selezione
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    colors = MenuDefaults.itemColors(
                        textColor = activityColor,
                        disabledTextColor = Color.Gray,
                        leadingIconColor = activityColor,
                        disabledLeadingIconColor = Color.Gray,
                        trailingIconColor = activityColor,
                        disabledTrailingIconColor = Color.Gray
                    )
                )
            }
        }
    }
}
