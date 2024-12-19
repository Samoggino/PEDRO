package com.lam.pedro.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch

@Composable
fun MoreScreen(
    navController: NavHostController,
    healthConnectManager: HealthConnectManager
) {

    val coroutineScope = rememberCoroutineScope()
    //val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(85.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(
                    RoundedCornerShape(26.dp)
                )
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = {
                    navController.navigate(Screen.LoginScreen.route) {
                        // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White), // Cambia il colore qui
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Account",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(
                    RoundedCornerShape(26.dp)
                )
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = {
                    navController.navigate(Screen.HealthConnectScreen.route) {
                        // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    } // Cambia lo stato del click
                })
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.health_connect_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White), // Cambia il colore qui
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Health Connect",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(
                    RoundedCornerShape(26.dp)
                )
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = {
                    navController.navigate(Screen.SettingScreen.route) {
                        // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.settings_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White), // Cambia il colore qui
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Settings",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(
                    RoundedCornerShape(26.dp)
                )
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = {
                    navController.navigate(Screen.PrivacyPolicy.route) {
                        // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    } // Cambia lo stato del click
                })
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.privacy_policy_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White), // Cambia il colore qui
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Privacy policy",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(
                    RoundedCornerShape(26.dp)
                )
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = {
                    navController.navigate(Screen.AboutScreen.route) {
                        // See: https://developer.android.com/jetpack/compose/navigation#nav-to-composable
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    } // Cambia lo stato del click
                })
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.about_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White), // Cambia il colore qui
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "About",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .clip(
                    RoundedCornerShape(26.dp)
                )
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = {
                    coroutineScope.launch {
                        val allActivities = fetchFromHealthConnectForDB(healthConnectManager)
                        // Usa `allActivities` come necessario
                    }
                })
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.modify_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White), // Cambia il colore qui
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "UPDATE DB",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )

        }

    }


}