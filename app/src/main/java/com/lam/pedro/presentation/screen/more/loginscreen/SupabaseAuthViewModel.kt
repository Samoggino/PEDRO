package com.lam.pedro.presentation.screen.more.loginscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lam.pedro.data.datasource.SecurePreferencesManager.logoutSecurePrefs
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
import kotlinx.coroutines.launch

class SupabaseAuthViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var errorMessage by mutableStateOf("")
    var showErrorDialog by mutableStateOf(false)
    var isLoading by mutableStateOf(false)


    /**
     * Funzione per il login.
     *
     * Questa funzione verifica le credenziali dell'utente e, in caso di successo,
     * naviga verso la schermata di lettura dei dati di Health Connect.
     *
     * @param navController Il controller di navigazione.
     */
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
                navController.navigate(Screen.MyScreenRecords.route)
            } else {
                errorMessage = "Email o password errati."
                showErrorDialog = true
            }
            isLoading = false
        }
    }


    /**
     * Funzione per il login con Supabase.
     *
     * Questa funzione tenta di autenticare l'utente utilizzando le credenziali fornite.
     *
     * @param email L'email dell'utente.
     * @param password La password dell'utente.
     * @return La sessione utente se il login ha successo, altrimenti null.
     */
    private suspend fun logInAuth(email: String, password: String): UserSession? {
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
                saveTokens(session.accessToken, session.refreshToken, session.user?.id)
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


    /**
     * Funzione per il logout dell'utente.
     *
     * Questa funzione pulisce la sessione e reindirizza l'utente alla schermata di login.
     *
     * @param navController Il controller di navigazione.
     */
    fun logout(navController: NavController) {
        viewModelScope.launch {
            try {
                val supabase = supabase()

                // pulisci la sessione
                supabase.auth.sessionManager.deleteSession()
                supabase.auth.signOut()
                logoutSecurePrefs()

                Log.d("Supabase", "Logout avvenuto con successo")

                navController.navigate(Screen.LoginScreen.route)
            } catch (e: Exception) {
                Log.e("Supabase", "Errore durante il logout: ${e.message}")
            }
        }
    }


    fun checkUserLoggedIn(fromLogin: Boolean) {
        viewModelScope.launch {
            val session = userSession()
            if (session != null) {
//                navController.navigate(Screen.ExerciseSessionData.route)
            } else {
                if (!fromLogin) {
                    errorMessage = "Effettua il login per accedere"
                    showErrorDialog = true
                } else {
//                    navController.navigate(Screen.LoginScreen.route)
                }
            }
        }
    }
}

/**
 * Factory per la creazione di un [SupabaseAuthViewModel].
 *
 * @return Un'istanza di [SupabaseAuthViewModel].
 */
class SupabaseAuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupabaseAuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SupabaseAuthViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}