package com.lam.pedro.data.datasource.authRepository

import android.util.Log
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveProfileInfo
import com.lam.pedro.data.datasource.SecurePreferencesManager.saveTokens
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkCredentials
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.userExists
import com.lam.pedro.presentation.screen.more.loginscreen.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthRepositoryImpl : IAuthRepository {

    override suspend fun login(email: String, password: String): UserSession? {
        if (!checkCredentials(email, password)) return null
        if (!userExists(email)) return null

        return try {
            supabase().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            // Carica la sessione
            val session = supabase().auth.sessionManager.loadSession()

            // Salva il token di accesso e il refresh token
            if (session != null) {
                session.user?.id?.let { saveTokens(session.accessToken, session.refreshToken, it) }
                Log.d("Supabase", "Login success: ${session.accessToken}")
            }

            updateUserInfo(session?.user?.id)

            session

        } catch (e: Exception) {
            Log.e("Login", "Errore di login: ${e.message}")
            null
        }
    }

    override suspend fun register(email: String, password: String): SignUpResult {
        try {
            supabase().auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val session = supabase().auth.sessionManager.loadSession()
            saveTokens(
                accessToken = session?.accessToken ?: "",
                refreshToken = session?.refreshToken ?: "",
                id = session?.user?.id
            )
            updateUserInfo(session?.user?.id)

            return SignUpResult.Success(session)
        } catch (e: AuthRestException) {
            return when (e.statusCode) {
                400 -> SignUpResult.Error("Invalid email or password")
                409 -> SignUpResult.UserAlreadyExists
                else -> SignUpResult.Error("Registration failed: ${e.message}")
            }
        } catch (e: Exception) {
            return SignUpResult.Error("An unexpected error occurred")
        }
    }

    private fun updateUserInfo(userId: String?) {

        try {
            CoroutineScope(Dispatchers.IO).launch {
                // salva in sharedPreferences username e avatar
                val user = supabase().from("users").select {
                    filter {
                        eq("id", userId!!)
                    }
                }.decodeSingle<User>()

                saveProfileInfo(username = user.username, avatarUrl = user.avatarUrl)
            }
        } catch (e: Exception) {
            Log.e(
                "UpdateUserInfo",
                "Errore durante l'aggiornamento delle informazioni utente: ${e.message}"
            )
        }
    }
}

/**
 * Sigillo di risposta per il risultato della registrazione.
 */
sealed class SignUpResult {
    data class Success(val session: UserSession?) : SignUpResult()
    data object UserAlreadyExists : SignUpResult()
    data class Error(val message: String) : SignUpResult()
}

data class RegisterFormData(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val confirmPassword: String = ""
)

data class LoginFormData(
    val email: String = "",
    val password: String = ""
)