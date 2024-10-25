package com.lam.pedro.data

import androidx.health.connect.client.units.Length

data class RunData(
    val distance: Length,              // Distanza percorsa
    val duration: java.time.Duration,  // Durata dell'attività
    val id: String,                    // ID del record
    val time: String,                  // Data e ora della registrazione
    val sourceAppInfo: String?         // Informazioni sull'app di origine
)