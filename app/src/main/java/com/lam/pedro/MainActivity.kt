package com.lam.pedro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lam.pedro.ui.theme.PEDROTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    try {
//        install(Auth)
        install(Postgrest)
    } catch (e: Exception) {
        System.err.println("Errore durante l'installazione di Postgrest: ${e.message}")
        e.printStackTrace()
    }
}


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
                    TodoList()
                }
            }
        }
    }
}

suspend fun fetchTodos(): List<TodoItem> {
    return try {
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
fun TodoList() {
    var items by remember { mutableStateOf<List<TodoItem>>(listOf()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Avvia la chiamata per ottenere i TodoItems
    LaunchedEffect(Unit) {
        Log.d("SupabaseData", "Avvio recupero dei dati")
        coroutineScope.launch {
            try {
                val fetchedItems = withContext(Dispatchers.IO) {
                    fetchTodos()
                }
                Log.d("SupabaseData", "Dati ricevuti: $fetchedItems")
                items = fetchedItems
                errorMessage = null
            } catch (e: Exception) {
                Log.e("SupabaseError", "Errore durante il recupero: ${e.message}")
                errorMessage = "Errore durante il recupero dei dati: ${e.message}"
            }
        }
    }

    LazyColumn {
        // Mostra il messaggio di errore se presente
        if (errorMessage != null) {
            item {
                Text(
                    errorMessage!!,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.error // Imposta il colore del messaggio di errore
                )
            }
        }

        // Mostra gli elementi solo se non ci sono errori
        if (items.isNotEmpty()) {
            items(items, key = { item -> item.id }) { item ->
                Text(
                    item.name.toString(),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        if (items.isEmpty()) {
            item {
                Text(
                    "Nessun elemento da mostrare",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
