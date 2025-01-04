package com.lam.pedro.presentation.screen.more.loginscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lam.pedro.R
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkUserLoggedIn

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = LoginViewModel
) {

    LaunchedEffect(true) {
        // check if user is already logged in
        checkUserLoggedIn(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }

                },
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

            val formData by viewModel.loginFormData.collectAsState()
            val isPasswordVisible by viewModel.isLoginPasswordVisible.collectAsState()
            val showDialog by viewModel.showLoginDialog.collectAsState()

            val email = formData.email
            val password = formData.password
            val state by viewModel.state.collectAsState()


            LoginRegisterDescriptor("\uD83C\uDF35Rejoin us Gringos!\uD83C\uDF2E")

            EmailField(
                value = email,
                onValueChange = { viewModel.updateLoginFormData(formData.copy(email = it)) },
                label = "Email",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp)),
            )

            PasswordTextField(
                value = password,
                onValueChange = { viewModel.updateLoginFormData(formData.copy(password = it)) },
                isPasswordVisible = isPasswordVisible,
                label = "Password",
                onVisibilityChange = { viewModel.toggleLoginPasswordVisibility() },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp)),
            )

            // Login button
            TextButton(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text("Login")
            }

            /**
             *  TODO: capire come fare il "password dimenticata"
             */
            TextButton(onClick = {
                // do something to recover password
            }) {
                Text("Forgot password?")
            }

            TextButton(onClick = { navController.navigate(Screen.RegisterScreen.route) }) {
                Text("You are not registered yet? Join us!")
            }

            if (state is LoadingState.Loading) {
                CircularProgressIndicator()
            }

            WelcomeDialog(
                showDialog = showDialog,
                dialogState = state,
                onDismiss = { viewModel.hideDialog() },
                onNavigate = { navController.navigate(Screen.HomeScreen.route) }
            )

            // aggiungi il logout
            TextButton(onClick = { viewModel.logout(navController) }) {
                Text("Logout")
            }

        }
    }
}
