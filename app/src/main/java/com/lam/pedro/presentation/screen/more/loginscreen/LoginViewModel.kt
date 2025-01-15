package com.lam.pedro.presentation.screen.more.loginscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.authRepository.IAuthRepository
import com.lam.pedro.data.datasource.authRepository.LoginFormData
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class LoginViewModel(private val authRepository: IAuthRepository) : ViewModel() {

    private val _loginFormData = MutableStateFlow(LoginFormData())
    val loginFormData: StateFlow<LoginFormData> = _loginFormData.asStateFlow()

    // Funzione per aggiornare i dati del modulo di login
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

            val session = authRepository.login(email, password)
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
}

class LoginViewModelFactory(
    private val authRepository: IAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}