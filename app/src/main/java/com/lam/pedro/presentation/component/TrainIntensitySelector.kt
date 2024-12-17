package com.lam.pedro.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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