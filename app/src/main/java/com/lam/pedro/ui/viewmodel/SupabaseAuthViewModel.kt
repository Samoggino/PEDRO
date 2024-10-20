package com.lam.pedro.ui.viewmodel

import android.util.Log
import com.lam.pedro.data.datasource.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession

// ViewModel per la gestione dell'autenticazione con Supabase, prende in input l'email e la password
// e restituisce un token di autenticazione in caso di successo
class SupabaseAuthViewModel {
    private fun checkCredentials(email: String, password: String): Boolean {

        Log.d("Supabase-Auth", "checkCorrect $email $password")

        // controlla se l'email è vuota
        if (email.isEmpty()) {
            Log.e("Supabase-Auth", "ERRORE: signInAuth: Email vuota")
            return false
        }

        // email deve contenere almeno un punto e una chiocciola
        if (!email.contains("@") || !email.contains(".")) {
            Log.e("Supabase-Auth", "ERRORE: signInAuth: Email non valida")
            return false
        }

        // controlla se la password è vuota
        if (password.isEmpty()) {
            Log.e("Supabase-Auth", "ERRORE: signInAuth: Password vuota")
            return false
        }

        // controlla se la password contiene almeno 8 caratteri
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

        // controlla se l'email è già presente sul db
        try {
            SupabaseClientProvider.getSupabaseClient().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("Supabase", "signInAuth: Successo")
            val session = SupabaseClientProvider.getSupabaseClient().auth.currentSessionOrNull()
            Log.d("Supabase", "signInAuth: Token: ${session?.accessToken}")
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

        if (!checkCredentials(email, password)) return null

        try {
            SupabaseClientProvider.getSupabaseClient().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("Supabase", "signInAuth: Successo")
            val session = SupabaseClientProvider.getSupabaseClient().auth.currentSessionOrNull()
            Log.d("Supabase", "signInAuth: Token: ${session?.accessToken}")
            return session
        } catch (e: AuthRestException) {
            Log.e("Supabase", "ERRORE: signInAuth: ${e.message}")
        } catch (e: Exception) {
            Log.e("Supabase", "ERRORE: signInAuth: ${e.message}")
        }

        return null
    }

}

