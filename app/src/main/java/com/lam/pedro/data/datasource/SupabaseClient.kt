package com.lam.pedro.data.datasource

import android.util.Log
import com.lam.pedro.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject

object SupabaseClient {

    private val lazyClient: SupabaseClient by lazy {
        try {
            createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseKey = BuildConfig.SUPABASE_KEY
            ) {
                try {
                    install(Auth)
                    install(Postgrest)
                    install(Storage)
                    install(Realtime)
                } catch (e: Exception) {
                    System.err.println("Errore durante la connessione a Supabase: ${e.message}")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante la creazione del client Supabase: ${e.message}")
            throw e
        }
    }

    suspend fun supabase(): SupabaseClient {
        return withContext(Dispatchers.IO) {
            lazyClient
        }
    }

    suspend fun userSession(): UserSession? {
        return withContext(Dispatchers.IO) {
            lazyClient.auth.currentSessionOrNull()
        }
    }

    /**
     * Funzione sicura per eseguire una RPC su Supabase, gestendo errori di inizializzazione e autenticazione.
     */
    suspend fun safeRpcCall(rpcFunctionName: String, jsonFinal: JsonObject): Any? {
        try {
            // Esegui la RPC
            val client = supabase()
            return client.postgrest.rpc(rpcFunctionName, jsonFinal)

        } catch (e: UnauthorizedRestException) {
            Log.e("Supabase", "Autenticazione fallita: ${e.message}")
            // Gestire il caso di errore di autenticazione (ad esempio, tentare di fare il login)
            return handleUnauthorizedException()

        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante la chiamata RPC: ${e.message}")
            throw e // Rilancia l'eccezione o gestisci altri tipi di errori
        }
    }

    private fun handleUnauthorizedException(): Any? {
        // Logica per gestire l'errore di autenticazione, come il ri-autenticarsi
        // Esegui di nuovo la RPC dopo aver risolto il problema di autenticazione
        Log.d("Supabase", "Tentando di ripristinare la sessione utente...")
        // Aggiungi logica per ripristinare la sessione o eseguire un login
        return null // O ritenta la RPC dopo aver risolto l'autenticazione
    }
}
