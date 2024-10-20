package com.lam.pedro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lam.pedro.data.datasource.SupabaseClientProvider
import com.lam.pedro.ui.theme.PEDROTheme
import io.github.jan.supabase.postgrest.from


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PEDROTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

suspend fun fetchTodos(): List<TodoItem> {
    return try {
        val supabase = SupabaseClientProvider.getSupabaseClient()

        val result = supabase
            .from("todos")
            .select()
            .decodeList<TodoItem>()

        Log.d("Supabase", "Fetched todos: $result") // Log dei risultati
        result
    } catch (e: Exception) {
        Log.e("SupabaseError", "Errore: ${e.message}", e) // Log dettagliato con stacktrace
        emptyList() // Ritorna una lista vuota in caso di errore
    }
}

@Composable
fun MyApp() {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = "login") {
//        composable("login") { LoginScreen(navController) }
//    }
}



