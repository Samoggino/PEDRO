package com.lam.pedro.presentation.screen.more.loginscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveTokens
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.data.datasource.SupabaseClient.userSession
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkCredentials
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.userExists
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object RegisterViewModel : ViewModel() {

    data class RegisterFormData(
        val email: String = "",
        val password: String = "",
        val username: String = "",
        val confirmPassword: String = "",
    )

    private val _formData = MutableStateFlow(RegisterFormData())
    val formData: StateFlow<RegisterFormData> = _formData.asStateFlow()

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

    /**
     * Sigillo di risposta per il risultato della registrazione.
     */
    sealed class SignUpResult {
        data class Success(val session: UserSession?) : SignUpResult()
        data object UserAlreadyExists : SignUpResult()
        data class Error(val message: String) : SignUpResult()
    }


    fun hideDialog() {
        _showDialog.value = false
    }

    /**
     * Funzione per la registrazione.
     *
     */
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

            if (!checkCredentials(email, password)) {
                _state.value =
                    LoadingState.Error("Credenziali non valide", true) // Show dialog
                _showDialog.value = true
                return@launch
            }
            if (userExists(email)) {
                _state.value = LoadingState.Error("Utente già registrato", true) // Show dialog
                _showDialog.value = true
                return@launch
            }

            when (val result = signUpSupabase()) {
                is SignUpResult.Success -> {
                    _state.value = LoadingState.Success("Welcome to the Gringos community!", true)
                    _showDialog.value = true // Show the dialog
                }

                is SignUpResult.UserAlreadyExists -> {
                    _state.value = LoadingState.Error("Utente già registrato")
                }

                is SignUpResult.Error -> {
                    _state.value = LoadingState.Error(result.message)
                }
            }
        }
    }

    /**
     * Funzione per la registrazione con Supabase.
     *
     * @return Il risultato della registrazione.
     */
    private suspend fun signUpSupabase(): SignUpResult {

        val email = formData.value.email
        val password = formData.value.password

        try {
            supabase().auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante la registrazione: ${e.message}", e)
        }

        return try {

            val session = userSession()
            saveTokens(
                accessToken = session?.accessToken ?: "",
                refreshToken = session?.refreshToken ?: "",
                id = session?.user?.id
            )

            setUsername()

            Log.d("Supabase", "Registrazione avvenuta: ${session?.accessToken}")
            SignUpResult.Success(session)

        } catch (e: AuthRestException) {
            Log.e("Supabase", "Registration error: ${e.message}")
            when (e.statusCode) { // Example: Check status code for specific errors
                400 -> SignUpResult.Error("Invalid email or password")
                409 -> SignUpResult.UserAlreadyExists
                else -> SignUpResult.Error("Registration failed: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("Supabase", "Generic registration error: ${e.message}")
            SignUpResult.Error("An unexpected error occurred")
        }
    }

    private fun setUsername() {

        viewModelScope.launch(Dispatchers.IO) {
            val uuid = getUUID()
            val email = formData.value.email
            val username = formData.value.username.ifEmpty { email.substringBefore("@") }

            try {
                supabase().from("users")
                    .update(
                        { set("username", username) }
                    ) { filter { eq("id", uuid!!) } }

                Log.d("Supabase", "Username set: $username")

            } catch (e: Exception) {
                Log.e("Supabase", "Errore durante il settaggio dell'username: ${e.message}", e)
            }
        }

    }
}

class RegisterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}