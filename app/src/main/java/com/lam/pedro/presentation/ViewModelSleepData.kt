package com.lam.pedro.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.healthconnectsample.data.HealthConnectManager
import com.lam.pedro.data.SleepSessionData
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.data.datasource.SupabaseClientProvider.supabase
import com.lam.pedro.presentation.navigation.Screen
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

class ViewModelSleepData : ViewModel() {

    // Funzione aggiornata
    suspend fun uploadSleepSession(
        navController: NavController,
        healthConnectManager: HealthConnectManager,
        sleepSessionData: SleepSessionData,
        context: Context
    ) {

        try {
            // logga il token dell'utente dalle sharedprefs
            // leggi il file di sharedprefs e printa l'accessToken e il refreshToken

            if (SecurePreferencesManager.getAccessToken(context) == null) {
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
            val response = supabase()
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

    suspend fun getSleepSessions(): List<SleepSessionData> {
        val response = supabase()
            .from("sleep_sessions")
            .select()
            .decodeList<SleepSessionData>()

        return response ?: emptyList()
    }
}

@Suppress("UNCHECKED_CAST")
class ViewModelSleepDataFactory :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelSleepData::class.java)) {
            return ViewModelSleepData() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}