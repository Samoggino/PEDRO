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
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
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
        viewModelScope.launch {
            isLoading = true
            when (val result = signUpAuth(email, password)) {
                is SignUpResult.Success -> navController.navigate(Screen.LandingScreen.route)
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
    private suspend fun logInAuth(email: String, password: String): UserSession? {
        if (!checkCredentials(email, password)) return null
        if (!userExists(email)) return null

        return try {
            SupabaseClientProvider.getSupabaseClient().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // prova a caricare la sessione
            val session =
                SupabaseClientProvider.getSupabaseClient().auth.sessionManager.loadSession()

//            session = SupabaseClientProvider.getSupabaseClient().auth.currentSessionOrNull()

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
    fun checkUserLoggedIn(navController: NavController) {
        viewModelScope.launch {
            val session = SupabaseClientProvider.getSupabaseClient().auth.currentSessionOrNull()


            if (session != null) {
                Log.d("Supabase", "Utente già loggato")
                navController.navigate(Screen.LandingScreen.route)
            } else {
                Log.d("Supabase", "Utente non loggato")
            }
        }
    }

}


