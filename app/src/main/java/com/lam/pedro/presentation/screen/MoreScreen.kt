package com.lam.pedro.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lam.pedro.R
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.fetchFromHealthConnectForDB
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkUserLoggedIn
import com.lam.pedro.presentation.screen.more.loginscreen.LoginState
import com.lam.pedro.presentation.serialization.ViewModelRecords
import kotlinx.coroutines.launch

@Composable
fun MoreScreen(
    navController: NavHostController,
    healthConnectManager: HealthConnectManager
) {
    val coroutineScope = rememberCoroutineScope()
    val loginState = remember { mutableStateOf<LoginState>(LoginState.Idle) }

    // Controllo login al montaggio del composable
    LaunchedEffect(Unit) {
        loginState.value = LoginState.Loading
        val result = checkUserLoggedIn() // Chiama la funzione sospesa
        loginState.value = result
    }

    val isLoggedIn = loginState.value is LoginState.LoggedIn

    // se l'utente è loggato c'è un bottone in più, quindi ogni elemento deve essere più piccolo
    val itemHeight = if (isLoggedIn) 75 else 85


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // Mostra l'elemento account
        AccountItem(navController, loginState.value)

        // Bottoni principali
        MenuItem(
            iconId = R.drawable.health_connect_icon,
            label = "Health Connect",
            onClick = { navController.navigate(Screen.HealthConnectScreen.route) },
            height = itemHeight
        )

        MenuItem(
            iconId = R.drawable.settings_icon,
            label = "Settings",
            onClick = { navController.navigate(Screen.SettingScreen.route) },
            height = itemHeight

        )

        MenuItem(
            iconId = R.drawable.privacy_policy_icon,
            label = "Privacy Policy",
            onClick = { navController.navigate(Screen.PrivacyPolicy.route) },
            height = itemHeight
        )

        MenuItem(
            iconId = R.drawable.about_icon,
            label = "About",
            onClick = { navController.navigate(Screen.AboutScreen.route) },
            height = itemHeight
        )

        MenuItem(
            iconId = R.drawable.modify_icon,
            label = "UPDATE DB",
            onClick = {
                coroutineScope.launch {
                    val allActivities = fetchFromHealthConnectForDB(healthConnectManager)
                    val viewModelRecords = ViewModelRecords()
                    viewModelRecords.insertActivitySession(allActivities)
                }
            },
            height = itemHeight
        )

        // Bottone Logout (mostrato solo se l'utente è loggato)
        if (loginState.value is LoginState.LoggedIn) {
            MenuItem(
                iconId = R.drawable.stop_icon,
                label = "Logout",
                onClick = {
                    coroutineScope.launch {
                        LoginRegisterHelper.logout()
                        // Aggiorna lo stato del login
                        loginState.value = checkUserLoggedIn()
                    }
                },
                extraIcon = {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Red, shape = CircleShape)
                    )
                }
            )
        }
    }
}

@Composable
private fun AccountItem(navController: NavHostController, loginState: LoginState) {
    when (loginState) {
        is LoginState.Loading -> CircularProgressIndicator()

        is LoginState.LoggedIn -> {
            MenuItem(
                iconId = R.drawable.user_icon,
                label = "You are a Gringo!",
                onClick = { },
                topHeight = 85,
                extraIcon = {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Green, shape = CircleShape)
                    )
                }
            )
        }

        is LoginState.NotLoggedIn -> {
            MenuItem(
                iconId = R.drawable.user_icon,
                label = "You're not a Gringo, yet",
                onClick = { navController.navigate(Screen.LoginScreen.route) },
                topHeight = 85
            )
        }

        else -> {}
    }
}


@Composable
fun MenuItem(
    iconId: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    topHeight: Int = 16,
    height: Int = 75,
    extraIcon: @Composable (() -> Unit)? = null // Nuovo parametro per l'icona extra
) {
    Spacer(modifier = Modifier.height(topHeight.dp))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )

        // Mostra l'icona extra se presente
        extraIcon?.invoke()

        Icon(
            Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(25.dp)
        )
    }
}
