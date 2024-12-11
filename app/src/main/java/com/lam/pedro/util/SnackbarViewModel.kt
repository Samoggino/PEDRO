package com.lam.pedro.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SnackbarViewModel : ViewModel() {
    val snackbarMessage = mutableStateOf("")

    // Funzione per aggiornare e mostrare il messaggio nel Snackbar
    suspend fun showSnackbar(snackbarHostState: SnackbarHostState, message: String) {
        snackbarMessage.value = message
        snackbarHostState.showSnackbar(message)
    }
}
