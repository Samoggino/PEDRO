package com.lam.pedro.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.ui.viewmodel.SupabaseAuthViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel = SupabaseAuthViewModel(
        context = LocalContext.current
    )

    // Stato per gestire la sessione utente
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }

    // CoroutineScope per gestire le chiamate asincrone
    val coroutineScope = rememberCoroutineScope()

    // Usa LaunchedEffect per gestire chiamate asincrone solo una volta
    LaunchedEffect(Unit) {
        try {
            val session = viewModel.getCurrentSession()
            Log.d("Supabase", "Sessione recuperata: $session")
            isLoggedIn = session != null
            Log.d("Supabase", "isLoggedIn impostato a: $isLoggedIn")
        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante il recupero della sessione utente: ${e.message}")
            isLoggedIn = false
        }
    }

    // Controlla lo stato di isLoggedIn
    when (isLoggedIn) {
        null -> {}

        false -> {
            // mostra che l'utente non è loggato e reindirizza alla pagina di login
            Log.e("Supabase", "Utente non loggato")
            AlertDialog(
                onDismissRequest = {
                    navController.navigate("login")
                },
                title = {
                    Text("Utente non loggato")
                },
                text = {
                    Text("Per favore, effettua il login per accedere alla Home Page")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            navController.navigate("login")
                        }
                    ) {
                        Text("Login")
                    }
                }
            )
        }

        true -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to the Home Page",
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // aggiungi un bottone per testare il logout
                Button(onClick = {
                    coroutineScope.launch {
                        try {
                            // chiama la funzione di logout
                            viewModel.logOut()
                            Log.d("Supabase", "Logout effettuato con successo")
                            isLoggedIn =
                                false // Imposta isLoggedIn su false per mostrare il dialogo di login
                            navController.navigate("login")
                        } catch (e: Exception) {
                            Log.e("Supabase", "Errore durante il logout: ${e.message}")
                        }
                    }
                }) {
                    Text(
                        text = "Logout",
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}
