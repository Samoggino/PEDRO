package com.lam.pedro.presentation.screen.more.loginscreen

import android.util.Log
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.data.datasource.SupabaseClient.userSession
import com.lam.pedro.presentation.screen.loginscreen.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

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

    suspend fun checkUserLoggedIn(fromLogin: Boolean): LoadingState {
        val session = userSession()
        return if (session != null) {
            if (fromLogin) {
                LoadingState.Success("Login avvenuto con successo", true)
            } else {
                LoadingState.Success("Utente già loggato", true)
            }
        } else {
            LoadingState.Error("Utente non loggato", true)
        }
    }
}