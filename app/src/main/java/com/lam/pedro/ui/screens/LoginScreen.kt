package com.lam.pedro.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import androidx.navigation.NavController
import com.lam.pedro.ui.viewmodel.SupabaseAuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen() {
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    val viewModel = SupabaseAuthViewModel() // Crea un'istanza del ViewModel
    val coroutineScope = rememberCoroutineScope() // Crea un coroutine scope

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
//            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo Email
        TextField(
            value = emailValue,
            onValueChange = { emailValue = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
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
//                    if (viewModel.logInAuth(emailValue, passwordValue) != null) {

                    // redirect to HomePage
                    Log.d("Supabase", "LoginScreen: pre-redirect")
                    try {
//                        navController.navigate("home")  // Naviga a HomeScreen
                    } catch (e: Exception) {
                        Log.e("Supabase", "ERRORE: Failed to redirect to HomePage")
                    }
                    Log.d("Supabase", "LoginScreen: post-redirect")

//                    } else {
                    // Handle login failure
//                    }
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
    }
}
