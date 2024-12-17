package com.lam.pedro.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
