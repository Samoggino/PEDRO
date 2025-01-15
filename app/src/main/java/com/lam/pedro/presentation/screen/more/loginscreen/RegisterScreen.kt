package com.lam.pedro.presentation.screen.more.loginscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.datasource.authRepository.AuthRepositoryImpl
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.navigation.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavBack: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(
            authRepository = AuthRepositoryImpl()
        )
    )
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Join us",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = { BackButton(onNavBack = onNavBack) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val formData by viewModel.formData.collectAsState()
            val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()

            val showDialog by viewModel.showDialog.collectAsState()
            val signUpState by viewModel.state.collectAsState()

            val email = formData.email
            val username = formData.username
            val password = formData.password
            val confirmPassword = formData.confirmPassword

            LoginRegisterDescriptor("\uD83C\uDF35Join the Gringos!\uD83C\uDF2E")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Campo Email
                PersonalInfoField(
                    value = email,
                    onValueChange = { viewModel.updateFormData(formData.copy(email = it)) },
                )

                // Campo Username
                PersonalInfoField(
                    value = username,
                    label = "Username",
                    onValueChange = { viewModel.updateFormData(formData.copy(username = it)) },
                    icon = Icons.Default.AccountBox,
                )

                // Campo Password
                PasswordTextField(
                    value = password,
                    onValueChange = { viewModel.updateFormData(formData.copy(password = it)) },
                    label = "Password",
                    isPasswordVisible = isPasswordVisible,
                    onVisibilityChange = { viewModel.togglePasswordVisibility() }
                )

                // Campo Conferma Password
                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = { viewModel.updateFormData(formData.copy(confirmPassword = it)) },
                    label = "Conferma Password",
                    isPasswordVisible = isPasswordVisible,
                    onVisibilityChange = { viewModel.togglePasswordVisibility() }
                )
            }


            // Pulsante di registrazione
            Button(
                onClick = { viewModel.signUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text("Join")
            }

            TextButton(onClick = { onNavigate(Screen.LoginScreen.route) }) {
                Text("Already have an account?")
            }

            WelcomeDialog(
                showDialog = showDialog,
                dialogState = signUpState, // Pass signUpState to the dialog
                onDismiss = { viewModel.hideDialog() },
                onNavigate = { onNavigate(Screen.HomeScreen.route) }
            )

        }
    }
}

@Composable
fun WelcomeDialog(
    showDialog: Boolean,
    dialogState: LoadingState,
    onDismiss: () -> Unit,
    onNavigate: () -> Unit // Aggiungi il callback di navigazione
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                if (dialogState is LoadingState.Success) {
                    onNavigate() // Naviga solo in caso di successo
                }
            },
            title = {
                when (dialogState) {
                    is LoadingState.Success -> Text("Benvenuto!")
                    is LoadingState.Error -> Text("Errore")
                    else -> {}
                }
            },
            text = {
                when (dialogState) {
                    is LoadingState.Success -> Text("\uD83C\uDF35${dialogState.message}\uD83C\uDF2E")
                    is LoadingState.Error -> Text(dialogState.message)
                    else -> {}
                }
            },
            confirmButton = {
                Button(onClick = {
                    onDismiss()
                    if (dialogState is LoadingState.Success) {
                        onNavigate() // Naviga solo in caso di successo
                    }
                }) {
                    Text("OK")
                }
            }
        )
    }
}
