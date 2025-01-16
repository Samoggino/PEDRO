package com.lam.pedro.presentation.onboarding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NextButtonUI(
    text: String = "Next",
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    fontSize: Int = 14,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick, colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, contentColor = textColor
        ), shape = RoundedCornerShape(26.dp)
    ) {
        Text(
            text = text, fontSize = fontSize.sp, style = textStyle
        )
    }
}