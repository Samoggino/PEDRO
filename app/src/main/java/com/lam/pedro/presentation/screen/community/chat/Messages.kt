package com.lam.pedro.presentation.screen.community.chat

import com.lam.pedro.presentation.screen.more.loginscreen.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
data class Message(
    val message: String,
    val timestamp: String,
    val sender: User
) {
    val formattedTimestamp: String
        get() {
            return try {
                val dateTime = LocalDateTime.parse(timestamp.substring(0, 19)) // Ignora i millisecondi e il suffisso 'Z'
                val currentDate = LocalDateTime.now(ZoneId.systemDefault())

                if (dateTime.toLocalDate() == currentDate.toLocalDate()) {
                    // Se è oggi, restituisci solo l'ora e i minuti
                    dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                } else {
                    // Altrimenti, restituisci la data
                    dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                }
            } catch (e: Exception) {
                // Gestione fallback in caso di errore nel parsing
                timestamp
            }
        }
}

@Serializable
data class Chat(
    @SerialName("uuidCHAT") val uuidCHAT: String,
    @SerialName("user1") val user1: String,
    @SerialName("user2") val user2: String,
    @SerialName("conversation") val conversation: List<Message> = emptyList()
)
