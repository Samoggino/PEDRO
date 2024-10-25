package com.lam.pedro.data

data class StepsData(
    val stepCount: Long,               // Numero di passi registrati
    val id: String,                     // ID del record
    val time: String,                   // Data e ora della registrazione
    val sourceAppInfo: String?          // Informazioni sull'app di origine
)
