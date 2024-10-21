package com.lam.pedro.ui.screens


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.ui.theme.WhiteGray
import com.lam.pedro.ui.theme.successGreen
import com.lam.pedro.ui.viewmodel.SupabaseAuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    // Crea un coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Crea un'istanza del SupabaseAuthViewModel
    val viewModel = SupabaseAuthViewModel(
        context = LocalContext.current
    )


    // Stato per mostrare il popup
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") } // Per memorizzare il messaggio di errore

    // Funzione per chiudere il popup
    fun closeErrorDialog() {
        showErrorDialog = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titolo
        Text(
            text = "Accedi",
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo Email
        TextField(
            value = emailValue,
            onValueChange = { emailValue = it },
            label = { Text("Email") },
            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Password
        TextField(
            value = passwordValue,
            onValueChange = { passwordValue = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(Icons.Default.Visibility, contentDescription = "Mostra password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        // Pulsante di accesso
        Button(
            onClick = {
                coroutineScope.launch {
                    val result = viewModel.logInAuth(emailValue, passwordValue)

                    // Mostra il popup di successo
                    if (result != null) {
                        showSuccessDialog = true
                        Log.d("Supabase", "LoginScreen: pre-redirect")

                    } else {
                        // Crea un messaggio di errore
                        errorMessage = "Email o password non corretti. Riprova."
                        showErrorDialog = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text("Accedi")
        }
        // Link Password dimenticata
        TextButton(onClick = { /* Handle Forgot Password */ }) {
            Text("Password dimenticata?")
        }

        // Pulsante Registrati
        TextButton(onClick = {
            coroutineScope.launch {
                viewModel.signInAuth(emailValue, passwordValue)
            }
        }) {
            Text("Non hai un account? Registrati")
        }

        // Popup di successo
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = {
                    Text(
                        text = "Login Riuscito",
                        color = successGreen, // Verde per indicare successo
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                },
                text = {
                    Text(
                        text = "Sei stato autenticato con successo!",
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showSuccessDialog = false },
                        colors = ButtonDefaults.buttonColors(successGreen) // Pulsante verde
                    ) {
                        Text("OK", color = WhiteGray)

                        // redirect alla HomePage
                        navController.navigate("home")
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            )
        }

        // Popup di errore
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { closeErrorDialog() }, // Chiudi se clicchi fuori dal popup
                title = {
                    Text(
                        text = "Errore di accesso",
                        color = Color.Red, // Rosso per indicare errore
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                },
                text = {
                    Text(
                        text = errorMessage, // Mostra il messaggio di errore
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { closeErrorDialog() }, // Chiudi il popup
                        colors = ButtonDefaults.buttonColors(Color.Red) // Pulsante rosso
                    ) {
                        Text("OK", color = Color.White)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
