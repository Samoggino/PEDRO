package com.lam.pedro.presentation.screen.more

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lam.pedro.presentation.component.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavBack: () -> Unit,
    titleId: Int
) {

    var isToggled by remember { mutableStateOf(false) }
    var isDarkMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = { BackButton(onNavBack) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Row for Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = "Prova",
                    style = MaterialTheme.typography.bodyMedium
                )

                Switch(
                    checked = isToggled,
                    onCheckedChange = { isChecked ->
                        isToggled = isChecked
                    }
                )
            }

            // Button to toggle Dark Mode
            Button(
                onClick = {
                    isDarkMode = !isDarkMode
                    // Apply dark mode change using the theme
                    // Usually, this would require re-composing or calling some theme change logic
                    // You might need to use a custom theme provider or a state management solution.
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode")
            }
        }
    }
}
