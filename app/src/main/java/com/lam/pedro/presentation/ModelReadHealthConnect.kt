package com.lam.pedro.presentation

import android.util.Log
import androidx.navigation.NavController
import com.example.healthconnectsample.data.HealthConnectManager
import com.example.healthconnectsample.data.SleepSessionData
import com.lam.pedro.data.datasource.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class ModelReadHealthConnect {


    suspend fun uploadSleepSession(
        navController: NavController,
        healthConnectManager: HealthConnectManager,
        sleepSessionData: SleepSessionData
    ) {
        try {
            SupabaseClientProvider.getSupabaseClient()
                .from("sleep_sessions")
                .upsert(sleepSessionData) // Passa direttamente l'oggetto

            Log.i(
                "Supabase-HealthConnect",
                "Dati della sessione di sonno caricati in ReadDataScreen"
            )
        } catch (e: Exception) {
            Log.e(
                "Supabase-HealthConnect",
                "Errore durante il caricamento dei dati della sessione di sonno",
                e
            )
        }
    }

    // fai la get dei dati
    suspend fun getSleepSessions(): List<SleepSessionData> {
        val response = SupabaseClientProvider.getSupabaseClient()
            .from("sleep_sessions")
            .select()
            .decodeList<SleepSessionData>()

        return response ?: emptyList()
    }


}
