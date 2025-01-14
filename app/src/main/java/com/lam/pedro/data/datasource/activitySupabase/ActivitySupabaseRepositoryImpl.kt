package com.lam.pedro.data.datasource.activitySupabase

import android.net.Uri
import android.util.Log
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.data.datasource.SupabaseClient.safeRpcCall
import com.lam.pedro.data.datasource.SupabaseClient.supabase
import com.lam.pedro.presentation.serialization.JsonDBManager
import com.lam.pedro.presentation.serialization.SessionCreator
import com.lam.pedro.presentation.serialization.SessionCreator.activityConfigs
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
class ActivitySupabaseRepositoryImpl : IActivityRepository {

    override suspend fun getActivitySession(
        activityEnum: ActivityEnum,
        uuid: String
    ): List<GenericActivity> {

        try {
            val config = activityConfigs[activityEnum] as? SessionCreator.ActivityConfig<*>

            if (config != null) {
                return Json.decodeFromString(
                    ListSerializer(config.responseType.serializer()),
                    (safeRpcCall(
                        rpcFunctionName = "get_${activityEnum.name.lowercase()}_session",
                        buildJsonObject {
                            put("user_uuid", Json.encodeToJsonElement(uuid))
                        }
                    ) as? PostgrestResult)?.data!!
                )
            }
        } catch (e: Exception) {
            Log.e("ActivityRepository", "Errore durante il recupero delle attività: $e")
        }
        return emptyList()
    }

    override suspend fun insertActivitySession(activity: GenericActivity, userUUID: String) {
        try {
            safeRpcCall(
                rpcFunctionName = "insert_${activity.activityEnum.name.lowercase()}_session",
                createActivityJson(activity, userUUID)
            )
        } catch (e: Exception) {
            Log.e(
                "ActivityRepository",
                "Errore durante l'inserimento: ${activity.activityEnum.name}",
                e
            )
        }
    }

    override suspend fun insertActivitySession(
        activities: List<GenericActivity>,
        userUUID: String
    ) {
        activities.forEach { activity ->
            try {
                // Inserisci la sessione nel database
                safeRpcCall(
                    rpcFunctionName = "insert_${activity.activityEnum.name.lowercase()}_session",
                    createActivityJson(activity, userUUID)
                )
            } catch (e: Exception) {
                Log.e(
                    "ActivityRepository",
                    "Errore durante l'inserimento: ${activity.activityEnum.name}",
                    e
                )
            }
        }
    }

    private fun createActivityJson(activity: GenericActivity, userUUID: String): JsonObject {
        return buildJsonObject {
            put("input_data", buildJsonObject {
                put("data", Json.encodeToJsonElement(activity))
                put("user_UUID", userUUID)
            })
        }
    }

    override suspend fun exportDataFromDB(uuid: String): String {
        return supabase()
            .from("activity")
            .select(
                columns = Columns.list(
                    "user_id",
                    "activity_type",
                    "start_time",
                    "end_time",
                    "notes",
                    "title",
                    "JSON"
                )
            ) {
                filter { eq("user_id", uuid) }
            }
            .decodeList<JsonObject>().toString()
    }

    override suspend fun importJsonToDatabase(uri: Uri, userUUID: String) {
        try {
            val inputStream =
                SecurePreferencesManager.getMyContext().contentResolver.openInputStream(uri)
                    ?: throw Exception("Impossibile leggere il file, URI non valido.")

            val jsonObject =
                Json.parseToJsonElement(inputStream.bufferedReader().use { it.readText() })
                    .jsonArray
                    .map { it.jsonObject }

            // Inserimento dati nel DB
            insertActivitySession(
                JsonDBManager.fromJsonListToGenericActivityList(jsonObject),
                userUUID
            )

            withContext(Dispatchers.IO) {
                inputStream.close()
            }

        } catch (e: Exception) {
            Log.e("ActivityRepository", "Errore durante l'import", e)
            throw e
        }
    }
}

