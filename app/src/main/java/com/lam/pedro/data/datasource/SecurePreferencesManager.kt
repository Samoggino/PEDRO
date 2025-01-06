package com.lam.pedro.data.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

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
     * Restituisce lo UUID dell'utente su Supabase.
     * Se non è presente, restituisce null.
     */
    fun getUUID(): String? {
        checkInitialized()
        return encryptedPrefs!!.getString(UUID, null)
    }

    /**
     * Restituisce il context dell'applicazione.
     */
    fun getMyContext(): Context {
        checkInitialized()
        return appContext!!
    }

}
