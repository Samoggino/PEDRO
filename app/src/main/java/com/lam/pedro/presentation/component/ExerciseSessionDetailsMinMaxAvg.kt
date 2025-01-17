
package com.lam.pedro.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.lam.pedro.R
import com.lam.pedro.presentation.theme.PedroTheme

/**
 * Shows the statistical min, max and average values, as can be returned from Health Platform.
 */
@Composable
fun ExerciseSessionDetailsMinMaxAvg(
    minimum: String?,
    maximum: String?,
    average: String?
) {
    Row {
        Text(
            modifier = Modifier
                .weight(1f),
            text = stringResource(
                R.string.label_and_value,
                stringResource(R.string.min_label),
                minimum ?: "N/A"
            ),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = stringResource(
                R.string.label_and_value,
                stringResource(R.string.max_label),
                maximum ?: "N/A"
            ),
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = stringResource(
                R.string.label_and_value,
                stringResource(R.string.avg_label),
                average ?: "N/A"
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun ExerciseSessionDetailsMinMaxAvgPreview() {
    PedroTheme {
        ExerciseSessionDetailsMinMaxAvg(minimum = "10", maximum = "100", average = "55")
    }
}
