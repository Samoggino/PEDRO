package com.lam.pedro.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.lam.pedro.data.datasource.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession

class SupabaseAuthViewModel(private val context: Context) {

    companion object {
        private const val PREFERENCES_NAME = "user_preferences"
        private const val KEY_TOKEN = "auth_token"
    }

    // Salva il token nelle SharedPreferences
    private fun saveToken(token: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_TOKEN, token)
        editor.apply()  // Salva il token in modo asincrono
    }

    // Recupera il token dalle SharedPreferences
    private fun getToken(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    // Cancella il token dalle SharedPreferences
    private fun clearToken() {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_TOKEN)
        editor.apply()  // Rimuove il token in modo asincrono
    }

    /**
     * Controlla la correttezza delle credenziali inserite
     * */
    private fun checkCredentials(email: String, password: String): Boolean {
        Log.d("Supabase-Auth", "checkCorrect $email $password")

        if (email.isEmpty()) {
            Log.e("Supabase-Auth", "ERRORE: signInAuth: Email vuota")
            return false
        }

        if (!email.contains("@") || !email.contains(".")) {
            Log.e("Supabase-Auth", "ERRORE: signInAuth: Email non valida")
            return false
        }

        if (password.isEmpty()) {
            Log.e("Supabase-Auth", "ERRORE: signInAuth: Password vuota")
            return false
        }

        if (password.length < 8) {
            Log.e("Supabase", "ERRORE: signInAuth: Password troppo corta")
            return false
        }

        return true
    }


    /**
     * Funzione per la registrazione con Supabase
     */
    suspend fun signInAuth(email: String, password: String): UserSession? {
        if (!checkCredentials(email, password)) return null

        try {
            SupabaseClientProvider.getSupabaseAuth().signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("Supabase", "signInAuth: Successo")
            val session = SupabaseClientProvider.getSupabaseAuth().currentSessionOrNull()

            // Salva il token della sessione
            session?.let {
                saveToken(it.accessToken)
                Log.d("Supabase", "signInAuth: Token salvato: ${it.accessToken}")
            }

            return session
        } catch (e: AuthRestException) {
            Log.e("Supabase", "ERRORE: signInAuth: ${e.message}")
        } catch (e: Exception) {
            Log.e("Supabase", "ERRORE: signInAuth: ${e.message}")
        }

        return null
    }

    /**
     * Funzione per il login con Supabase
     */
    suspend fun logInAuth(email: String, password: String): UserSession? {
        val supabase = SupabaseClientProvider.getSupabaseClient()
        val currentSession = getCurrentSession()


        if (!checkCredentials(email, password)) return null

        // se l'utente ha una sessione attiva, ritorna la sessione

        if (getToken() != null) {
            Log.d("Token", "logInAuth: Utente già loggato")
            return currentSession
        }

        try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("Supabase", "logInAuth: Successo")
            val session =
                supabase.auth.currentSessionOrNull()

            // Salva il token della sessione
            session?.let {
                saveToken(it.accessToken)
                Log.d("Token", "logInAuth: Token salvato: ${it.accessToken}")
                logCurrentToken()
            }

            return session
        } catch (e: AuthRestException) {
            Log.e("Token", "ERRORE: logInAuth: ${e.message}")
        } catch (e: Exception) {
            Log.e("Token", "ERRORE: logInAuth: ${e.message}")
        }

        return null
    }

    private fun logCurrentToken() {
        val token = getToken()
        Log.d("Supabase", "Current token: $token")
    }


    // Effettua il logout e cancella il token
    suspend fun logOut() {
        clearToken()
        SupabaseClientProvider.getSupabaseAuth().signOut()
        Log.d("Supabase", "logOut: Utente disconnesso e token cancellato")
    }


    // se il token è presente, ritorna la sessione corrente
    suspend fun getCurrentSession(): UserSession? {
        return SupabaseClientProvider.getSupabaseAuth().currentSessionOrNull()
    }

}
