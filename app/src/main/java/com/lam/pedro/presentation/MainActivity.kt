package com.lam.pedro.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.presentation.theme.PedroTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * The entry point
 */
class MainActivity : ComponentActivity() {

    // Stato che indica se i singleton sono stati inizializzati
    private val isInitialized = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)

        val context = this

        lifecycleScope.launch {
            // Inizializzazione dei singleton
            initializeSingleton(context)

            // Una volta completato, cambia lo stato per caricare la UI
            isInitialized.value = true
        }

        setContent {
            // Se i singleton sono inizializzati, carica la tua app, altrimenti mostra un placeholder
            if (isInitialized.value) {
                PedroTheme {
                    PedroApp()
                }
            } else {
                // Mostra un placeholder (es. loading spinner)
                LoadingScreen()
            }
        }
    }

    private suspend fun initializeSingleton(context: MainActivity) {
        // Inizializza il SecurePreferencesManager
        CoroutineScope(Dispatchers.IO).async {
            SecurePreferencesManager.initialize(context)
        }.await()
    }
}

// Un semplice composable per il placeholder di caricamento
@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}
