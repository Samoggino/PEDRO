package com.lam.pedro.data.datasource

import com.lam.pedro.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseClientProvider {

    // Inizializzazione lazy del client
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

    // Funzione sospesa per recuperare il client in modo asincrono
    suspend fun getSupabaseClient(): SupabaseClient {
        return withContext(Dispatchers.IO) {
            lazyClient // Questo garantisce che l'inizializzazione avvenga su un thread I/O
        }
    }
}