package com.lam.pedro.presentation.onboarding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NextButtonUI(
    text: String = "Next",
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    fontSize: Int = 14,
    isEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) backgroundColor else Color.Gray, // Cambia il colore di sfondo se disabilitato
            contentColor = if (isEnabled) textColor else Color.LightGray // Cambia il colore del testo se disabilitato
        ),
        shape = RoundedCornerShape(26.dp),
        enabled = isEnabled // Disabilita il bottone se `isEnabled` è false
    ) {
        Text(
            text = text, fontSize = fontSize.sp, style = textStyle
        )
    }
}
