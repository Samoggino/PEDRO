package com.lam.pedro.presentation.screen.community.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
data class Message(
    val uuidMessage: String = UUID.randomUUID().toString(), // Genera un UUID casuale
    val message: String,
    val timestamp: String,
    val sender: String
) {
    val formattedTimestamp: String
        get() {
            return try {
                val dateTime = LocalDateTime.parse(timestamp.substring(0, 19))
                val currentDate = LocalDateTime.now(ZoneId.systemDefault())

                // Se è oggi, restituisci solo l'ora e i minuti
                // Altrimenti, restituisci la data
                if (dateTime.toLocalDate() == currentDate.toLocalDate()) {
                    dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                } else {
                    dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                }
            } catch (e: Exception) {
                // in caso di errore nel parsing
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
