package com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen

import androidx.compose.runtime.Composable

// ScreenContext.kt
class ScreenContext(private val functionalities: List<ScreenFunctionality>) {

    @Composable
    fun ExecuteFunctionalities() {
        functionalities.forEach { functionality ->
            functionality.Execute() // Esegui la logica di ogni funzionalità
        }
    }
}
