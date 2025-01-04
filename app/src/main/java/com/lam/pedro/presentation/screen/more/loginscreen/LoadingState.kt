package com.lam.pedro.presentation.screen.more.loginscreen

/**
 * Stato del caricamento.
 */
sealed class LoadingState {
    data object Idle : LoadingState()
    data object Loading : LoadingState()
    data class Error(val message: String, val showDialog: Boolean = true) : LoadingState()
    data class Success(val message: String, val showDialog: Boolean = true) : LoadingState()
}