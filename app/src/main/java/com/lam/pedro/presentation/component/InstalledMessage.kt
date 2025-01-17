
package com.lam.pedro.presentation.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.lam.pedro.R
import com.lam.pedro.presentation.theme.PedroTheme

/**
 * Welcome text shown when the app first starts, where the Healthcore APK is already installed.
 */
@Composable
fun InstalledMessage() {
    Text(
        text = stringResource(id = R.string.installed_welcome_message),
        textAlign = TextAlign.Justify
    )
}

@Preview
@Composable
fun InstalledMessagePreview() {
    PedroTheme {
        InstalledMessage()
    }
}
