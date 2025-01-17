package com.lam.pedro.presentation.screen.more

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lam.pedro.R
import com.lam.pedro.data.HealthConnectManager
import com.lam.pedro.data.datasource.activitySupabase.ActivitySupabaseSupabaseRepositoryImpl
import com.lam.pedro.data.fetchFromHealthConnectForDB
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.CustomSnackbarHost
import com.lam.pedro.presentation.navigation.Screen
import com.lam.pedro.presentation.screen.MenuItem
import com.lam.pedro.presentation.screen.more.loginscreen.LoginRegisterHelper.checkUserLoggedIn
import com.lam.pedro.presentation.screen.more.loginscreen.LoginState
import com.lam.pedro.presentation.serialization.MyRecordsViewModel
import com.lam.pedro.util.notification.areNotificationsActive
import com.lam.pedro.util.notification.cancelPeriodicNotifications
import com.lam.pedro.util.notification.schedulePeriodicNotifications
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onNavBack: () -> Unit,
    onNavigate: (String) -> Unit,
    titleId: Int,
) {
    val loginState = remember { mutableStateOf<LoginState>(LoginState.Idle) }
    LaunchedEffect(Unit) {
        loginState.value = LoginState.Loading
        val result = checkUserLoggedIn() // Chiama la funzione sospesa
        loginState.value = result
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = { BackButton { onNavBack() } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp), // Margine orizzontale per uniformità
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Sezione per il pallino verde e il testo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Pallino verde
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                )

                Spacer(modifier = Modifier.width(8.dp)) // Spazio tra pallino e testo

                // Testo accanto al pallino
                Text(
                    text = "You're logged in, Hermano!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
                MenuItem(
                    iconId = R.drawable.upload_on_db_icon,
                    label = "Update remote DB",
                    onClick = {
                        coroutineScope.launch {
                            val allActivities =
                                fetchFromHealthConnectForDB(HealthConnectManager(context))
                            val viewModelRecords =
                                MyRecordsViewModel(ActivitySupabaseSupabaseRepositoryImpl())
                            viewModelRecords.insertActivitySession(allActivities)
                        }
                    },
                    height = 80,
                    finalIcon = Icons.Filled.TouchApp
                )


            Spacer(modifier = Modifier.height(24.dp))

            MenuItem(
                iconId = R.drawable.import_export_icon,
                label = "Import/export (JSON)",
                onClick = { onNavigate(Screen.ImportExportScreen.route) },
                height = 80
            )

            Spacer(modifier = Modifier.height(72.dp)) // Spazio tra il testo e il bottone

            // Bottone rosso per il logout
            Button(
                onClick = {
                    // Logica di logout qui
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // Sfondo rosso
                    contentColor = Color.White // Testo bianco
                ),
                modifier = Modifier.fillMaxWidth() // Il bottone occupa tutta la larghezza disponibile
            ) {
                Text(text = "Logout")
            }
        }
    }


}