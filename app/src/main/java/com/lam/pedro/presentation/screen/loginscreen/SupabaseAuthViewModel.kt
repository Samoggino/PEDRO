package com.lam.pedro.presentation.screen.loginscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lam.pedro.data.datasource.SupabaseClientProvider
import com.lam.pedro.presentation.navigation.Screen
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.launch

class SupabaseAuthViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var errorMessage by mutableStateOf("")
    var showErrorDialog by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    // Sigillo di risposta per il risultato della registrazione
    sealed class SignUpResult {
        data class Success(val session: UserSession?) : SignUpResult()
        data object UserAlreadyExists : SignUpResult()
        data class Error(val message: String) : SignUpResult()
    }

    // Funzione per il login
    fun login(navController: NavController) {
        if (!checkCredentials(email, password)) {
            errorMessage = "Credenziali non valide"
            showErrorDialog = true
            return
        }

        viewModelScope.launch {
            isLoading = true
            val session = logInAuth(email, password)
            if (session != null) {
                navController.navigate(Screen.LandingScreen.route)
            } else {
                errorMessage = "Email o password errati."
                showErrorDialog = true
            }
            isLoading = false
        }
    }

    // Funzione per la registrazione
    fun signUp(navController: NavController) {
        if (!checkCredentials(email, password)) {
            errorMessage = "Credenziali non valide"
            showErrorDialog = true
            return
        }

        viewModelScope.launch {
            isLoading = true
            val result = signUpAuth(email, password)
            when (result) {
                is SignUpResult.Success -> navController.navigate(Screen.LandingScreen.route)
                is SignUpResult.UserAlreadyExists -> navController.navigate(Screen.LandingScreen.route)
                is SignUpResult.Error -> {
                    errorMessage = result.message
                    showErrorDialog = true
                }
            }
            isLoading = false
        }
    }

    // Logica per il controllo delle credenziali
    private fun checkCredentials(email: String, password: String): Boolean {
        Log.d("Supabase-Auth", "checkCorrect $email $password")

        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            Log.e("Supabase-Auth", "ERRORE: signInAuth: Email non valida")
            return false
        }
        if (password.isEmpty() || password.length < 8) {
            Log.e("Supabase-Auth", "ERRORE: signInAuth: Password non valida")
            return false
        }
        return true
    }

    // Funzione per il login con Supabase
    suspend fun logInAuth(email: String, password: String): UserSession? {
        if (!checkCredentials(email, password)) return null

        return try {
            SupabaseClientProvider.getSupabaseClient().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val session = SupabaseClientProvider.getSupabaseClient().auth.currentSessionOrNull()
            Log.d("Supabase", "Login success: ${session?.accessToken}")
            session
        } catch (e: AuthRestException) {
            Log.e("Supabase", "Errore di login: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("Supabase", "Errore generico di login: ${e.message}")
            null
        }
    }

    // Funzione per la registrazione con Supabase
    suspend fun signUpAuth(email: String, password: String): SignUpResult {
        if (!checkCredentials(email, password)) return SignUpResult.Error("Credenziali non valide")

        return try {
            if (logInAuth(email, password) != null) {
                Log.e("Supabase", "Email già registrata")
                return SignUpResult.UserAlreadyExists
            }

            SupabaseClientProvider.getSupabaseClient().auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val session = SupabaseClientProvider.getSupabaseClient().auth.currentSessionOrNull()
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
