package com.lam.pedro.presentation.screen.more.loginscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.authRepository.IAuthRepository
import com.lam.pedro.data.datasource.authRepository.RegisterFormData
import com.lam.pedro.data.datasource.authRepository.SignUpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: IAuthRepository) : ViewModel() {

    private val _formData = MutableStateFlow(RegisterFormData()) // Usa la data class qui
    val formData: StateFlow<RegisterFormData> = _formData.asStateFlow()

    // Funzione per aggiornare i dati del modulo
    fun updateFormData(newFormData: RegisterFormData) {
        _formData.value = newFormData
    }

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _state = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val state: StateFlow<LoadingState> = _state.asStateFlow()

    fun hideDialog() {
        _showDialog.value = false
    }

    fun signUp() {
        val email = formData.value.email
        val password = formData.value.password
        val confirmPassword = formData.value.confirmPassword

        if (password != confirmPassword) {
            _state.value = LoadingState.Error("Passwords do not match")
            return
        }

        _state.value = LoadingState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = authRepository.register(email, password)) {
                is SignUpResult.Success -> {
                    _state.value = LoadingState.Success("Benvenuto!", true)
                }
                is SignUpResult.UserAlreadyExists -> {
                    _state.value = LoadingState.Error("Utente già registrato")
                }
                is SignUpResult.Error -> {
                    _state.value = LoadingState.Error(result.message)
                }
            }
            _showDialog.value = true
        }
    }
}

class RegisterViewModelFactory(
    private val authRepository: IAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}