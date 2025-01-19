package com.lam.pedro.data.datasource.authRepository

import io.github.jan.supabase.auth.user.UserSession

interface IAuthRepository {
    suspend fun login(email: String, password: String): UserSession?
    suspend fun register(email: String, password: String, username: String): SignUpResult
}