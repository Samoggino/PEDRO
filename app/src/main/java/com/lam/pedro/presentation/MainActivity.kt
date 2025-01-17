package com.lam.pedro.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.presentation.onboarding.OnboardingScreen
import com.lam.pedro.presentation.onboarding.OnboardingUtils
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
    private val onboardingUtils by lazy { OnboardingUtils(this) }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
            PedroTheme {
                // if (qualcosa) { TODO
                ShowOnboardingScreen()
                // } else {
                // PedroApp()
                // }
            }
        }
    }


    private suspend fun initializeSingleton(context: MainActivity) {
        // Inizializza il SecurePreferencesManager
        CoroutineScope(Dispatchers.IO).async {
            SecurePreferencesManager.initialize(context)
        }.await()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    private fun ShowOnboardingScreen() {

        if (isInitialized.value) {
            val scope = rememberCoroutineScope()
            OnboardingScreen {
                onboardingUtils.setOnboardingCompleted()

                scope.launch {
                    setContent {
                        PedroApp()
                    }
                }
            }
        } else {
            // Mostra un placeholder (es. loading spinner)
            LoadingScreen()
        }
    }
}


// Un semplice composable per il placeholder di caricamento
@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
