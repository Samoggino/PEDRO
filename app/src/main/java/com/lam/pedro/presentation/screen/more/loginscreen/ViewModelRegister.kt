package com.lam.pedro.presentation.screen.more.loginscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveTokens
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.data.datasource.SupabaseClient.userSession
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkCredentials
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.userExists
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ViewModelRegister : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> = _isPasswordVisible.asStateFlow()

    private val _isConfirmPasswordVisible = MutableStateFlow(false)
    val isConfirmPasswordVisible: StateFlow<Boolean> = _isConfirmPasswordVisible.asStateFlow()

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun toggleConfirmPasswordVisibility() {
        _isConfirmPasswordVisible.value = !_isConfirmPasswordVisible.value
    }

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)

    /**
     * Sigillo di risposta per il risultato della registrazione.
     */
    sealed class SignUpResult {
        data class Success(val session: UserSession?) : SignUpResult()
        object UserAlreadyExists : SignUpResult()
        data class Error(val message: String) : SignUpResult()
    }

    /**
     * Stati della registrazione.
     */
    sealed class SignUpState {
        object Idle : SignUpState()
        object Loading : SignUpState()
        data class Error(val message: String, val showDialog: Boolean = true) : SignUpState()
        object Success : SignUpState()
    }

    /**
     * Funzione per la registrazione.
     *
     * @param navController Il controller di navigazione.
     */
    fun signUp(
        navController: NavController,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (password != confirmPassword) {
            _signUpState.value = SignUpState.Error("Le password non corrispondono")
            return
        }

        _signUpState.value = SignUpState.Loading

        viewModelScope.launch {
            when (val result = signUpAuth(email, password)) {
                is SignUpResult.Success -> {
                    _signUpState.value = SignUpState.Success
                    navController.navigate(Screen.MyScreenRecords.route)
                }

                is SignUpResult.UserAlreadyExists -> {
                    _signUpState.value = SignUpState.Error("Utente già registrato")
                }

                is SignUpResult.Error -> {
                    _signUpState.value = SignUpState.Error(result.message)
                }
            }
        }
    }

    /**
     * Funzione per la registrazione con Supabase.
     *
     * @param email L'email dell'utente.
     * @param password La password dell'utente.
     * @return Il risultato della registrazione.
     */
    private suspend fun signUpAuth(
        email: String,
        password: String
    ): SignUpResult {
        if (!checkCredentials(email, password)) {
            return SignUpResult.Error("Credenziali non valide")
        }
        if (userExists(email)) {
            return SignUpResult.UserAlreadyExists
        }

        return try {
            supabase().auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val session = userSession()
            saveTokens(
                accessToken = session?.accessToken ?: "",
                refreshToken = session?.refreshToken ?: "",
                id = session?.user?.id
            )

            Log.d("Supabase", "Registrazione avvenuta: ${session?.accessToken}")
            SignUpResult.Success(session)

        } catch (e: AuthRestException) {
            Log.e("Supabase", "Errore di registrazione: ${e.message}")
            SignUpResult.Error("Errore: ${e.message}")
        } catch (e: Exception) {
            Log.e("Supabase", "Errore generico di registrazione: ${e.message}")
            SignUpResult.Error("Errore: ${e.message}")
        }
    }
}

/**
 * Factory per la creazione del ViewModel.
 */
class ViewModelRegisterFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelRegister::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelRegister() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}