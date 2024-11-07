package com.lam.pedro.presentation.serialization.exercisedata

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.lam.pedro.data.activity.ExerciseSessionData
import com.lam.pedro.data.datasource.SecurePreferencesManager.getAccessToken
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.SupabaseClientProvider.supabase
import com.lam.pedro.presentation.navigation.Screen
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

class ViewModelExerciseData : ViewModel() {

    suspend fun uploadExerciseSession(
        navController: NavController,
        exerciseSessionData: ExerciseSessionData,
        context: Context
    ) {
        try {


            if (getAccessToken(context) == null) {
                navController.navigate(Screen.LoginScreen.route)
            }

            val uuid = getUUID(context)


            // Crea un oggetto JSON completo con i dati della sessione di sonno e l'UUID dell'utente
            val jsonFinal = buildJsonObject {
                put("input_data", buildJsonObject {
                    put("data", Json.encodeToJsonElement(exerciseSessionData))
                    put("user_UUID", Json.encodeToJsonElement(uuid))
                })
            }


            Log.d("Supabase-HealthConnect", "Dati di esercizio da caricare $jsonFinal")

            // Esegui la chiamata RPC su Supabase
            supabase()
                .postgrest
                .rpc("insert_exercise_session", jsonFinal)

        } catch (e: Exception) {
            Log.e(
                "Supabase-HealthConnect",
                "Errore durante l'upload dei dati di esercizio ${e.message}"
            )
            Log.e("Supabase-HealthConnect", "Dati di esercizio da caricare $exerciseSessionData")
        }
    }


    suspend fun getExerciseSessions(): List<ExerciseSessionData> {

        try {
            val response = supabase()
                .from("exercise_sessions")
                .select()
                .decodeList<ExerciseSessionData>()
            
            return response

        } catch (e: Exception) {
            Log.e(
                "Supabase-HealthConnect",
                "Errore durante il recupero dei dati di esercizio ${e.message}"
            )
        }

        return emptyList()
    }

}

@Suppress("UNCHECKED_CAST")
class ViewModelExerciseDataFactory :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelExerciseData::class.java)) {
            return ViewModelExerciseData() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}