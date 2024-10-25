package com.lam.pedro.presentation.screen.loginscreen

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,  // UUID come stringa
    val email: String
)
