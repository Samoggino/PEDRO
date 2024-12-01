package com.lam.pedro.presentation.screen.loginscreen


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.presentation.component.LinkedApp

@Composable
fun LoginScreen(
    navController: NavController
) {
    val viewModel: SupabaseAuthViewModel = viewModel(factory = SupabaseAuthViewModelFactory())
    var isPasswordVisible by remember { mutableStateOf(false) }

    // al lancio fai una stampa
    LaunchedEffect(true) {
        Log.i("Supabase", "LaunchedEffect in LoginScreen")

        // controlla al mount che l'utente sia loggato o abbia un token
        viewModel.checkUserLoggedIn(navController, true)

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Accedi",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        LinkedApp(R.drawable.supabase_logo_icon)

        Spacer(modifier = Modifier.height(40.dp))

        // Campo Email
        TextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
        )


        TextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Nascondi password" else "Mostra password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(26.dp))
        )

        // Pulsante di accesso
        Button(
            onClick = { viewModel.login(navController) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text("Accedi")
        }

        TextButton(onClick = { /* Handle Forgot Password */ }) {
            Text("Password dimenticata?")
        }

        TextButton(onClick = { viewModel.signUp(navController) }) {
            Text("Non hai un account? Registrati")
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator()
        }

        if (viewModel.showErrorDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showErrorDialog = false },
                title = { Text("Errore di accesso") },
                text = { Text(viewModel.errorMessage) },
                confirmButton = {
                    Button(onClick = { viewModel.showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
        // aggiungi il logout
        TextButton(onClick = { viewModel.logout(navController) }) {
            Text("Logout")
        }

    }
}
