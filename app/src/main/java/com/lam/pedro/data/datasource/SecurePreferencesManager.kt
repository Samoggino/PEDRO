package com.lam.pedro.data.datasource

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession

object SecurePreferencesManager {
    private const val PREFS_NAME = "secure_prefs"
    private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"
    private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"
    private const val UUID = "UUID"


    private var encryptedPrefs: SharedPreferences? = null
    var appContext: Context? = null

    /**
     * Inizializza il SecurePreferencesManager con l'application context.
     *
     * @param context Il contesto dell'applicazione.
     */
    fun initialize(context: Context) {
        if (encryptedPrefs == null) {
            appContext = context.applicationContext
            val masterKey = MasterKey.Builder(appContext!!)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                appContext!!,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    private fun checkInitialized() {
        if (encryptedPrefs == null) {
            throw IllegalStateException("SecurePreferencesManager is not initialized. Call initialize(context) first.")
        }
    }

    /**
     * Salva i token di accesso e di aggiornamento nelle SharedPreferences crittografate.
     *
     * @param accessToken Il token di accesso da salvare.
     * @param refreshToken Il token di aggiornamento da salvare.
     */
    fun saveTokens(accessToken: String, refreshToken: String, id: String?) {
        checkInitialized()
        with(encryptedPrefs!!.edit()) {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            id?.let { putString(UUID, it) }
            apply()
        }
    }

    /**
     * Ottiene il token di accesso dalle SharedPreferences crittografate.
     *
     * @return Il token di accesso o null se non è presente.
     */
    private fun getAccessToken(): String? {
        checkInitialized()
        return encryptedPrefs!!.getString(ACCESS_TOKEN_KEY, null)
    }

    /**
     * Ottiene il token di aggiornamento dalle SharedPreferences crittografate.
     *
     * @return Il token di aggiornamento o null se non è presente.
     */
    private fun getRefreshToken(): String? {
        checkInitialized()
        return encryptedPrefs!!.getString(REFRESH_TOKEN_KEY, null)
    }

    /**
     * Verifica se l'utente è autenticato controllando se esiste un access token salvato.
     *
     * @return true se l'access token esiste, altrimenti false.
     */
    fun isUserAuthenticated(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }

    /**
     * Cancella tutti i dati relativi al login dalle SharedPreferences crittografate.
     */
    fun logoutSecurePrefs() {
        checkInitialized()

        // Specifica le chiavi relative al login da rimuovere
        val loginKeys = listOf(ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY, UUID)

        with(encryptedPrefs!!.edit()) {
            loginKeys.forEach { remove(it) } // Rimuovi solo le chiavi specificate
            apply()
        }
    }

    /**
     * Fa refresh della session con il refresh token
     *
     * @return La nuova sessione utente se il refresh ha successo, altrimenti null.
     * @see UserSession
     */
    suspend fun refreshSession(): UserSession? {
        // Funzione per recuperare il refresh token salvato
        val refreshToken = getRefreshToken()
        return if (refreshToken != null) {
            try {

                val session = supabase().auth.refreshSession(refreshToken)

                // Salva i nuovi token
                saveTokens(session.accessToken, session.refreshToken, session.user?.id)
                session.accessToken // Ritorna il nuovo access token

            } catch (e: Exception) {
                Log.e("Supabase", "Errore nel refresh del token: ${e.message}")
            }
            null
        } else {
            Log.e("Supabase", "Nessun refresh token trovato")
            null
        }
    }


    fun getUUID(): String? {
        checkInitialized()
        return encryptedPrefs!!.getString(UUID, null)
    }


}
