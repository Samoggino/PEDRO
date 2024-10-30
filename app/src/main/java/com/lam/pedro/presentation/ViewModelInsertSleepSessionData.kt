package com.lam.pedro.presentation

import android.util.Log
import androidx.navigation.NavController
import com.example.healthconnectsample.data.HealthConnectManager
import com.example.healthconnectsample.data.SleepSessionData
import com.lam.pedro.data.datasource.SupabaseClientProvider
import com.lam.pedro.presentation.navigation.Screen
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

class ViewModelInsertSleepSessionData {

    // Funzione aggiornata
    @OptIn(SupabaseInternal::class)
    suspend fun uploadSleepSession(
        navController: NavController,
        healthConnectManager: HealthConnectManager,
        sleepSessionData: SleepSessionData
    ) {

        try {

            // logga il token dell'utente
            Log.i("Supabase", "Token dell'utente: ${SupabaseClientProvider.getSupabaseClient().accessToken}")

            if (SupabaseClientProvider.getSupabaseClient().accessToken == null) {
               navController.navigate(Screen.LoginScreen.route)
            }



            // Crea un oggetto JSON completo con i dati della sessione di sonno e l'UUID dell'utente
            val jsonFinal = buildJsonObject {
                put("input_data", buildJsonObject {
                    put("data", Json.encodeToJsonElement(sleepSessionData))
                    put("user_UUID", "0539f7b7-ec21-4bd1-a8fe-bc8deb5e5260")
                })
            }

            // Logga i dati della sessione di sonno
            Log.d("Supabase", "Dati della sessione di sonno: $jsonFinal")

            // Esegui la chiamata RPC su Supabase
            val response = SupabaseClientProvider.getSupabaseClient()
                .postgrest
                .rpc("insert_sleep_session", jsonFinal)


            Log.i("Supabase", "Dati della sessione di sonno caricati in Supabase")

            Log.i(
                "Supabase-HealthConnect",
                "Dati della sessione di sonno caricati in ReadDataScreen"
            )
        } catch (e: Exception) {
            Log.e(
                "Supabase-HealthConnect",
                "Errore: ${e.message}",

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
