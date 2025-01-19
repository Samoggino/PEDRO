package com.lam.pedro.data.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.lam.pedro.presentation.screen.profile.ProfilePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SecurePreferencesManager {
    private const val PREFS_NAME = "secure_prefs"
    private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"
    private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"
    private const val UUID = "UUID"
    private const val USERNAME = "USERNAME"
    private const val AVATAR_URL = "AVATAR_URL"

    private const val ONBOARDING = "ONBOARDING"


    private var encryptedPrefs: SharedPreferences? = null
    private var appContext: Context? = null

    /**
     * Inizializza il SecurePreferencesManager con l'application context.
     *
     * @param context Il contesto dell'applicazione.
     */
    fun initialize(context: Context) {
        if (appContext != null) {
            return
        }

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

    /**
     * Verifica che SecurePreferencesManager sia stato inizializzato.
     */
    private fun checkInitialized() {
        if (appContext == null || encryptedPrefs == null) {
            throw IllegalStateException("SecurePreferencesManager is not initialized. Call initialize(context) first.")
        }
    }

    /**
     * Salva i token di accesso e di aggiornamento nelle SharedPreferences crittografate.
     *
     * @param accessToken Il token di accesso da salvare.
     * @param refreshToken Il token di aggiornamento da salvare.
     * @param id Il UUID da associare, se disponibile.
     */
    fun saveTokens(accessToken: String, refreshToken: String, id: String?) {
        checkInitialized()

        CoroutineScope(Dispatchers.IO).launch {
            with(encryptedPrefs!!.edit()) {
                putString(ACCESS_TOKEN_KEY, accessToken)
                putString(REFRESH_TOKEN_KEY, refreshToken)
                id?.let { putString(UUID, it) }
                apply()
            }
        }
    }

    fun saveProfileInfo(username: String, avatarUrl: String?) {
        checkInitialized()

        CoroutineScope(Dispatchers.IO).launch {
            with(encryptedPrefs!!.edit()) {
                putString(USERNAME, username)
                avatarUrl?.let { putString(AVATAR_URL, it) }
                apply()
            }
        }
    }

    /**
     * Cancella tutti i dati relativi al login dalle SharedPreferences crittografate.
     */
    fun logoutSecurePrefs() {
        checkInitialized()

        CoroutineScope(Dispatchers.IO).launch {
            val loginKeys = listOf(ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY, UUID, USERNAME, AVATAR_URL)

            with(encryptedPrefs!!.edit()) {
                loginKeys.forEach { remove(it) } // Rimuovi solo le chiavi specificate
                apply()
            }
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

    fun getUsername(): String? {
        checkInitialized()

        return encryptedPrefs!!.getString(USERNAME, null)
    }

    fun getAvatarUrl(): String? {
        checkInitialized()

        return encryptedPrefs!!.getString(AVATAR_URL, null)
    }

    fun updateAvatarUrl(avatarUrl: String?) {
        checkInitialized()

        CoroutineScope(Dispatchers.IO).launch {
            with(encryptedPrefs!!.edit()) {

                if (avatarUrl == null) {
                    remove(AVATAR_URL)
                } else {
                    putString(AVATAR_URL, avatarUrl)
                }
                apply()
            }
        }
    }


    /**
     * Restituisce il context dell'applicazione.
     * Se il manager non è stato inizializzato, lancerà un'eccezione.
     */
    fun getMyContext(): Context {
        checkInitialized()

        return appContext!!
    }


    // Metodi per salvare e recuperare le preferenze del profilo
    fun saveProfileData(preference: ProfilePreference, value: String) {
        checkInitialized()
        saveOnboardingCompleted()

        CoroutineScope(Dispatchers.IO).launch {
            with(encryptedPrefs!!.edit()) {
                putString(preference.key, value)
                apply()
            }
        }
    }

    fun getProfileData(preference: ProfilePreference): String {
        checkInitialized()

        return encryptedPrefs!!.getString(preference.key, "")!!
    }

    private fun saveOnboardingCompleted() {
        checkInitialized()

        CoroutineScope(Dispatchers.IO).launch {
            with(encryptedPrefs!!.edit()) {
                putBoolean(ONBOARDING, true)
                apply()
            }
        }
    }

    fun isOnboardingCompleted(): Boolean {
        checkInitialized()

        return encryptedPrefs!!.getBoolean(ONBOARDING, false)
    }
}

