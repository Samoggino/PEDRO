package com.lam.pedro.presentation.screen.more.loginscreen

import android.util.Log
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SecurePreferencesManager.logoutSecurePrefs
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.screen.loginscreen.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.withTimeoutOrNull

object LoginRegisterHelper {
    fun checkCredentials(email: String, password: String): Boolean {
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            Log.e("Supabase", "ERRORE: checkCredentials: Email non valida")
            return false
        }
        if (password.isEmpty() || password.length < 8) {
            Log.e("Supabase", "ERRORE: checkCredentials: Password non valida")
            return false
        }
        return true
    }


    /**
     * Verifica se l'utente esiste nel database.
     *
     * @param email L'email dell'utente da controllare.
     * @return True se l'utente esiste, altrimenti False.
     */
    suspend fun userExists(email: String): Boolean {
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

    /**
     * Controlla se l'utente è loggato.
     *
     * @return Lo stato del login.
     */
    suspend fun checkUserLoggedIn(): LoginState {
        return withTimeoutOrNull(5000L) { // Timeout di 5 secondi
            getUUID()?.let {
                LoginState.LoggedIn("Utente loggato")
            } ?: LoginState.NotLoggedIn("Utente non loggato")
        } ?: LoginState.Error("Timeout durante la verifica del login")
    }

    /**
     * Funzione per il logout dell'utente.
     */
    suspend fun logout() {

        try {
            val supabase = supabase()

            // pulisci la sessione
            supabase.auth.sessionManager.deleteSession()
            supabase.auth.signOut()
            logoutSecurePrefs()

            Log.d("Supabase", "Logout avvenuto con successo")

        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante il logout: ${e.message}")
        }
    }
}