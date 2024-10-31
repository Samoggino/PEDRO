package com.lam.pedro.data.datasource

import com.lam.pedro.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseClientProvider {

    /**
     * Inizializzazione del client
     */
    private val lazyClient: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            try {
                install(Auth)
                install(Postgrest)
            } catch (e: Exception) {
                System.err.println("Errore durante la connessione a Supabase: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Restituisce il client Supabase in modo asincrono.
     *
     * Questa funzione sospesa garantisce che l'inizializzazione del client avvenga
     * su un thread di I/O per evitare di bloccare il thread principale.
     *
     * @return Un'istanza di [SupabaseClient].
     */
    suspend fun supabase(): SupabaseClient {
        return withContext(Dispatchers.IO) {
            lazyClient // Questo garantisce che l'inizializzazione avvenga su un thread I/O
        }
    }

    /**
     * Restituisce una sessione utente se c'è, altrimenti return null.
     *
     * Questa funzione sospesa garantisce che l'inizializzazione del client avvenga
     * su un thread di I/O per evitare di bloccare il thread principale.
     *
     * @return Un'istanza di [UserSession].
     */
    suspend fun userSession(): UserSession? {
        return withContext(Dispatchers.IO) {
            lazyClient.auth.currentSessionOrNull()
        }
    }
}
