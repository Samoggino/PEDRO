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

    /**
     * Restituisce un'istanza di [SharedPreferences] crittografata.
     *
     * @param context Il contesto dell'applicazione.
     * @return Un'istanza di [SharedPreferences] crittografata.
     */
    private fun getPrefs(context: Context): SharedPreferences {
        if (encryptedPrefs == null) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        return encryptedPrefs!!
    }

    /**
     * Salva i token di accesso e di aggiornamento nelle SharedPreferences crittografate.
     *
     * @param accessToken Il token di accesso da salvare.
     * @param refreshToken Il token di aggiornamento da salvare.
     * @param context Il contesto dell'applicazione.
     */
    fun saveTokens(accessToken: String, refreshToken: String, context: Context, id: String?) {
        with(getPrefs(context).edit()) {
            putString(ACCESS_TOKEN_KEY, accessToken)
            putString(REFRESH_TOKEN_KEY, refreshToken)
            if (id != null) {
                putString(UUID, id)
            }
            apply()
        }
    }

    /**
     * Ottiene il token di accesso dalle SharedPreferences crittografate.
     *
     * @param context Il contesto dell'applicazione.
     * @return Il token di accesso o null se non è presente.
     */
    fun getAccessToken(context: Context): String? {
        return getPrefs(context).getString(ACCESS_TOKEN_KEY, null)
    }

    /**
     * Ottiene il token di aggiornamento dalle SharedPreferences crittografate.
     *
     * @param context Il contesto dell'applicazione.
     * @return Il token di aggiornamento o null se non è presente.
     */
    private fun getRefreshToken(context: Context): String? {
        return getPrefs(context).getString(REFRESH_TOKEN_KEY, null)
    }

    /**
     * Verifica se l'utente è autenticato controllando se esiste un access token salvato.
     *
     * @param context Il contesto dell'applicazione.
     * @return true se l'access token esiste, altrimenti false.
     */
    fun isUserAuthenticated(context: Context): Boolean {
        return !getAccessToken(context).isNullOrEmpty()
    }

    /**
     * Cancella tutti i dati memorizzati nelle SharedPreferences crittografate.
     * Utile per eseguire il logout dell'utente.
     *
     * @param context Il contesto dell'applicazione.
     */
    fun clearSecurePrefs(context: Context) {
        with(getPrefs(context).edit()) {
            clear()
            apply()
        }
    }

    /**
     * Fa refresh della session con il refresh token
     *
     * @param context Il contesto dell'applicazione.
     * @return La nuova sessione utente se il refresh ha successo, altrimenti null.
     * @see UserSession
     */
    suspend fun refreshSession(context: Context): UserSession? {
        // Funzione per recuperare il refresh token salvato
        val refreshToken = getRefreshToken(context)
        return if (refreshToken != null) {
            try {

                val session = supabase().auth.refreshSession(refreshToken)

                // Salva i nuovi token
                saveTokens(session.accessToken, session.refreshToken, context, session.user?.id)
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

    fun getUUID(context: Context): String? {
        return getPrefs(context).getString(UUID, null)
    }


}
