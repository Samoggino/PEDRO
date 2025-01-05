package com.lam.pedro.presentation.screen.more.loginscreen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("username") val username: String,
    // avatar url è un campo opzionale, quindi se non è presente nel JSON, verrà impostato su una stringa vuota
    @SerialName("avatar") val avatarUrl: String? = null
)

fun parseUsers(jsonString: String): Map<User, Boolean> {
    // Configura l'oggetto JSON
    val json = Json { ignoreUnknownKeys = true }

    // Decodifica il JSON direttamente nella lista desiderata
    val rawList = json.decodeFromString<List<RawUserWithFollowStatus>>(jsonString)

    // Trasforma la lista raw in una mappa di User a Boolean
    return rawList.associate { raw ->
        User(
            id = raw.userId,
            email = raw.userEmail,
            username = raw.username,
            avatarUrl = raw.avatarUrl ?: "" // Usa stringa vuota se null
        ) to raw.isFollowed
    }

}


@Serializable
private data class RawUserWithFollowStatus(
    @SerialName("user_id") val userId: String,
    @SerialName("user_email") val userEmail: String,
    @SerialName("user_avatar") val avatarUrl: String? = null,
    @SerialName("username") val username: String,
    @SerialName("is_following") val isFollowed: Boolean
)