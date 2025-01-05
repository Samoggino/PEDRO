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

/**
 * Stato del login.
 */
sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class LoggedIn(val message: String = "") : LoginState()
    data class NotLoggedIn(val message: String = "") : LoginState()
    data class Error(val message: String = "") : LoginState()
}