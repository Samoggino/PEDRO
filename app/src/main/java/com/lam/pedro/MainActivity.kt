package com.lam.pedro

import android.os.Bundle
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lam.pedro.ui.theme.PEDROTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


val supabase = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    try {

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

@Composable
fun TodoList() {
    var items by remember { mutableStateOf<List<TodoItem>>(listOf()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                items = supabase.from("todos")
                    .select()
                    .decodeList<TodoItem>()
                errorMessage = null // Resetta il messaggio di errore se la chiamata ha successo
            } catch (e: Exception) {
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
                    item.name,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
