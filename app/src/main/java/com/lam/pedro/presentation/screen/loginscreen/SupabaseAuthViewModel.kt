package com.lam.pedro.presentation.screen.loginscreen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lam.pedro.data.datasource.SecurePreferencesManager.clearSecurePrefs
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveTokens
import com.lam.pedro.data.datasource.SupabaseClientProvider.supabase
import com.lam.pedro.data.datasource.SupabaseClientProvider.userSession
import com.lam.pedro.presentation.navigation.Screen
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class SupabaseAuthViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var errorMessage by mutableStateOf("")
    var showErrorDialog by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    /**
     * Sigillo di risposta per il risultato della registrazione.
     */
    sealed class SignUpResult {
        data class Success(val session: UserSession?) : SignUpResult()
        data object UserAlreadyExists : SignUpResult()
        data class Error(val message: String) : SignUpResult()
    }

    /**
     * Funzione per il login.
     *
     * Questa funzione verifica le credenziali dell'utente e, in caso di successo,
     * naviga verso la schermata di lettura dei dati di Health Connect.
     *
     * @param navController Il controller di navigazione.
     * @param context Il contesto dell'applicazione.
     */
    fun login(navController: NavController, context: Context) {
        if (!checkCredentials(email, password)) {
            errorMessage = "Credenziali non valide"
            showErrorDialog = true
            return
        }

        viewModelScope.launch {
            isLoading = true
            val session = logInAuth(email, password, context)
            if (session != null) {
                navController.navigate(Screen.ReadHealthConnectData.route)
            } else {
                errorMessage = "Email o password errati."
                showErrorDialog = true
            }
            isLoading = false
        }
    }

    /**
     * Funzione per la registrazione.
     *
     * Questa funzione registra un nuovo utente e, in caso di successo,
     * naviga verso la schermata di lettura dei dati di Health Connect.
     *
     * @param navController Il controller di navigazione.
     */
    fun signUp(navController: NavController, context: Context) {
        viewModelScope.launch {
            isLoading = true
            when (val result = signUpAuth(email, password, context)) {
                is SignUpResult.Success -> navController.navigate(Screen.ReadHealthConnectData.route)
                is SignUpResult.UserAlreadyExists -> {
                    errorMessage = "Utente già registrato"
                    showErrorDialog = true
                }

                is SignUpResult.Error -> {
                    errorMessage = result.message
                    showErrorDialog = true
                }
            }
            isLoading = false
        }
    }

    /**
     * Logica per il controllo delle credenziali.
     *
     * @param email L'email dell'utente.
     * @param password La password dell'utente.
     * @return True se le credenziali sono valide, altrimenti False.
     */
    private fun checkCredentials(email: String, password: String): Boolean {
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

    /**
     * Funzione per il login con Supabase.
     *
     * Questa funzione tenta di autenticare l'utente utilizzando le credenziali fornite.
     *
     * @param email L'email dell'utente.
     * @param password La password dell'utente.
     * @param context Il contesto dell'applicazione.
     * @return La sessione utente se il login ha successo, altrimenti null.
     */
    private suspend fun logInAuth(email: String, password: String, context: Context): UserSession? {
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
                saveTokens(session.accessToken, session.refreshToken, context)
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
     * Funzione per la registrazione con Supabase.
     *
     * @param email L'email dell'utente.
     * @param password La password dell'utente.
     * @return Il risultato della registrazione.
     */
    private suspend fun signUpAuth(
        email: String,
        password: String,
        context: Context
    ): SignUpResult {
        if (!checkCredentials(email, password)) return SignUpResult.Error("Credenziali non valide")
        if (userExists(email)) return SignUpResult.UserAlreadyExists

        return try {
            supabase().auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val session = userSession()
            saveTokens(session?.accessToken ?: "", session?.refreshToken ?: "", context)

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
                clearSecurePrefs(navController.context)

                Log.d("Supabase", "Logout avvenuto con successo")

                navController.navigate(Screen.LoginScreen.route)
            } catch (e: Exception) {
                Log.e("Supabase", "Errore durante il logout: ${e.message}")
            }
        }
    }

    /**
     * Verifica se l'utente esiste nel database.
     *
     * @param email L'email dell'utente da controllare.
     * @return True se l'utente esiste, altrimenti False.
     */
    private suspend fun userExists(email: String): Boolean {
        return try {
            val userList = supabase()
                .from("users")
                .select(columns = Columns.list("id", "email")) {
                    filter {
                        eq("email", email)
                    }
                }.decodeList<User>()

            val userExists = userList.isNotEmpty()
            Log.d(
                "Supabase",
                "L'utente con email: $email esiste: $userExists"
            )
            userExists
        } catch (e: Exception) {
            Log.e("Supabase", "Errore nel controllo dell'utente: ${e.message}")
            false
        }
    }

    fun checkUserLoggedIn(navController: NavController, context: Context) {
        viewModelScope.launch {
            val session = userSession()
            if (session != null) {
                navController.navigate(Screen.ReadHealthConnectData.route)
            } else {
                navController.navigate(Screen.LoginScreen.route)
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