package com.lam.pedro.presentation.screen.loginscreen


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.lam.pedro.data.datasource.SupabaseClientProvider
import com.lam.pedro.presentation.navigation.Screen
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class SupabaseAuthViewModel(application: Application) : ViewModel() {

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

    // Funzione per la registrazione
    fun signUp(navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            when (val result = signUpAuth(email, password)) {
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

    // Logica per il controllo delle credenziali
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

    // Funzione per il login con Supabase
    private suspend fun logInAuth(email: String, password: String, context: Context): UserSession? {
        if (!checkCredentials(email, password)) return null
        if (!userExists(email)) return null

        return try {
            val response = SupabaseClientProvider.getSupabaseClient().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // Carica la sessione
            val session =
                SupabaseClientProvider.getSupabaseClient().auth.sessionManager.loadSession()

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

    // Funzione per salvare i token
    private fun saveTokens(accessToken: String, refreshToken: String, context: Context) {
        val prefs = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            apply()
        }
    }

    private suspend fun refreshAccessToken(context: Context): String? {
        val refreshToken =
            getRefreshToken(context) // Funzione per recuperare il refresh token salvato
        return if (refreshToken != null) {
            try {
                val session =
                    SupabaseClientProvider.getSupabaseClient().auth.refreshSession(refreshToken)

                // Salva i nuovi token
                saveTokens(session.accessToken, session.refreshToken, context)
                session.accessToken // Ritorna il nuovo access token
            } catch (e: Exception) {
                Log.e("Supabase", "Errore nel refresh del token: ${e.message}")
                null
            }
        } else {
            Log.e("Supabase", "Nessun refresh token trovato")
            null
        }
    }

    private fun getRefreshToken(context: Context): String? {
        val prefs = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
        return prefs.getString("REFRESH_TOKEN", null)
    }


    // Funzione per la registrazione con Supabase
    private suspend fun signUpAuth(email: String, password: String): SignUpResult {

        if (!checkCredentials(email, password)) return SignUpResult.Error("Credenziali non valide")
        if (userExists(email)) return SignUpResult.UserAlreadyExists


        return try {

            SupabaseClientProvider.getSupabaseClient()
                .auth
                .signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

            val session = SupabaseClientProvider.getSupabaseClient()
                .auth
                .currentSessionOrNull()

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

    fun logout(navController: NavController) {
        viewModelScope.launch {
            try {
                val supabaseClient = SupabaseClientProvider.getSupabaseClient()

                // pulisci la sessione
                supabaseClient.auth.sessionManager.deleteSession()
                supabaseClient.auth.signOut()


                Log.d("Supabase", "Logout avvenuto con successo")

                navController.navigate(Screen.LoginScreen.route)
            } catch (e: Exception) {
                Log.e("Supabase", "Errore durante il logout: ${e.message}")
            }
        }
    }

    private suspend fun userExists(email: String): Boolean {
        return try {
            val userList = SupabaseClientProvider.getSupabaseClient()
                .from("users")
                .select(columns = Columns.list("id", "email")) {
                    filter {
                        eq("email", email)
                    }
                }.decodeList<User>()

            val userExists = userList.isNotEmpty()
            Log.d(
                "Supabase",
                "L'utente con email: $email ${if (userExists) "esiste" else "non esiste"}. ${userList.count()}"
            )
            userExists

        } catch (e: Exception) {
            Log.e(
                "Supabase",
                "Errore durante il controllo dell'esistenza dell'utente: ${e.message}"
            )
            false
        }
    }


    // controlla che l'utente sia già loggato e abbia una session
    @OptIn(SupabaseInternal::class)
    fun checkUserLoggedIn(navController: NavController, context: Context) {

        viewModelScope.launch {
            val supabase = SupabaseClientProvider.getSupabaseClient()
            val session = supabase.auth.currentSessionOrNull()


            if (session != null) {
                if (supabase.accessToken != null) {
                    val newAccessToken = refreshAccessToken(context)
                    if (newAccessToken != null) {
                        Log.d("Supabase", "Token rinfrescato con successo")
                        navController.navigate(Screen.LandingScreen.route)
                    } else {
                        Log.e("Supabase", "Errore nel refresh del token, esegui logout")
                        logout(navController)
                        navController.navigate(Screen.LoginScreen.route)
                    }
                } else {
                    Log.e("Supabase", "Errore: Token non valido")
                    logout(navController)
                    navController.navigate(Screen.LoginScreen.route)
                }
            } else {
                Log.d("Supabase", "Utente non loggato")
            }

        }
    }

}

@Suppress("UNCHECKED_CAST")
class SupabaseAuthViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupabaseAuthViewModel::class.java)) {
            return SupabaseAuthViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


