package com.lam.pedro.presentation.screen.more.loginscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveTokens
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkCredentials
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.userExists
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object LoginViewModel : ViewModel() {

    data class LoginFormData(
        val email: String = "",
        val password: String = "",
    )

    private val _loginFormData = MutableStateFlow(LoginFormData())
    val loginFormData: StateFlow<LoginFormData> = _loginFormData.asStateFlow()

    fun updateLoginFormData(newFormData: LoginFormData) {
        _loginFormData.value = newFormData
    }

    private val _isLoginPasswordVisible = MutableStateFlow(false)
    val isLoginPasswordVisible: StateFlow<Boolean> = _isLoginPasswordVisible.asStateFlow()

    fun toggleLoginPasswordVisibility() {
        _isLoginPasswordVisible.value = !_isLoginPasswordVisible.value
    }

    private val _showLoginDialog = MutableStateFlow(false)
    val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

    fun hideDialog() {
        _showLoginDialog.value = false
    }

    private val _loginState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val state: StateFlow<LoadingState> = _loginState.asStateFlow()

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    /**
     * Funzione per il login.
     *
     * Questa funzione verifica le credenziali dell'utente e, in caso di successo,
     * naviga verso la schermata di lettura dei dati di Health Connect.
     *
     */
    fun login() {

        _loginState.value = LoadingState.Loading

        viewModelScope.launch(Dispatchers.IO) {

            val email = loginFormData.value.email
            val password = loginFormData.value.password

            if (!checkCredentials(email, password)) {
                _loginState.value = LoadingState.Error("Credenziali non valide", true)
                _showDialog.value = true

                return@launch
            }

            val session = logInAuth()
            if (session != null) {
                _loginState.value = LoadingState.Success("Login avvenuto con successo", true)
                _showDialog.value = true
            } else {
                _loginState.value = LoadingState.Error("Errore durante il login", true)
                _showDialog.value = true
            }
            _loginState.value = LoadingState.Idle
        }
    }


    /**
     * Funzione per il login con Supabase.
     *
     * Questa funzione tenta di autenticare l'utente utilizzando le credenziali fornite.
     *
     * @return La sessione utente se il login ha successo, altrimenti null.
     */
    private suspend fun logInAuth(): UserSession? {
        val email = loginFormData.value.email
        val password = loginFormData.value.password
        if (!checkCredentials(email, password)) return null
        if (!userExists(email)) return null

        return try {
            supabase().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // Carica la sessione
            val session = supabase().auth.sessionManager.loadSession()

            // Salva il token di accesso e il refresh token
            if (session != null) {
                session.user?.id?.let { saveTokens(session.accessToken, session.refreshToken, it) }
                Log.d("Supabase", "Login success: ${session.accessToken}")
            }

            session

        } catch (e: AuthRestException) {
            Log.e("Supabase", "Errore di login: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("Supabase", "Errore generico di login: ${e.message}")
            null
        }
    }
}
