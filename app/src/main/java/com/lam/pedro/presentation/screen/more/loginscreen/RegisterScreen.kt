package com.lam.pedro.presentation.screen.more.loginscreen

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.lam.pedro.presentation.navigation.Screen

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: ViewModelRegister = viewModel(factory = ViewModelRegisterFactory())
) {

    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val isConfirmPasswordVisible by viewModel.isConfirmPasswordVisible.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registrati",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(40.dp))

        LinkedApp(R.drawable.supabase_logo_icon)

        Spacer(modifier = Modifier.height(40.dp))

        // Campo Email
        EmailField(
            value = email,
            onValueChange = { viewModel.updateEmail(it) }
        )

        // Campo Password
        PasswordTextField(
            value = password,
            onValueChange = { viewModel.updatePassword(it) },
            label = "Password",
            isPasswordVisible = isPasswordVisible,
            onVisibilityChange = { viewModel.togglePasswordVisibility() }
        )

        // Campo Conferma Password
        PasswordTextField(
            value = confirmPassword,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            label = "Conferma Password",
            isPasswordVisible = isConfirmPasswordVisible,
            onVisibilityChange = { viewModel.toggleConfirmPasswordVisibility() }
        )


        // Pulsante di registrazione
        Button(
            onClick = { viewModel.signUp(navController, email, password, confirmPassword) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text("Registrati")
        }

        TextButton(onClick = { navController.navigate(Screen.LoginScreen.route) }) {
            Text("Hai già un account? Accedi")
        }

    }
}


@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Email", // Default label is "Email"
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPasswordVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onVisibilityChange(!isPasswordVisible) }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isPasswordVisible) "Nascondi password" else "Mostra password"
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(26.dp))
    )
}
