package com.lam.pedro.presentation.screen.more

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.navigation.NavHostController
import com.lam.pedro.R
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.CustomSnackbarHost
import com.lam.pedro.util.notification.areNotificationsActive
import com.lam.pedro.util.notification.cancelPeriodicNotifications
import com.lam.pedro.util.notification.schedulePeriodicNotifications
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    titleId: Int
) {
    val scrollState = rememberScrollState()
    var isToggled by remember { mutableStateOf(false) }
    var notificationInterval by remember { mutableLongStateOf(15L) } // Default to 15 minutes as Long
    var stepGoal by remember { mutableIntStateOf(0) } // Step goal
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Load initial settings
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        notificationInterval =
            sharedPreferences.getLong("notificationInterval", 15L) // Default to 15 minutes
        stepGoal = sharedPreferences.getInt("stepGoal", 5000) // Default to 5000 steps

        // Check if notifications are active
        areNotificationsActive(context) { isActive ->
            isToggled = isActive
        }
    }

    fun saveSettings() {
        // Save settings to SharedPreferences
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putLong("notificationInterval", notificationInterval) // Save as Long
            putInt("stepGoal", stepGoal)
            apply()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    BackButton(navController)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        var showDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Switch to enable/disable periodic notifications
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable periodic notifications",
                    style = MaterialTheme.typography.bodyLarge, // Increased text size
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "interval: $notificationInterval (set)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isToggled) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = if (isToggled) {
                            Modifier.clickable { showDialog = true }
                        } else {
                            Modifier
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Switch(
                        checked = isToggled,
                        onCheckedChange = { isChecked ->
                            isToggled = isChecked
                            if (isChecked) {
                                // Schedule notifications when toggle is enabled
                                schedulePeriodicNotifications(context, notificationInterval)
                            } else {
                                // Cancel notifications when toggle is disabled
                                cancelPeriodicNotifications(context)
                            }
                        }
                    )
                }
            }

            // Open dialog to set notification interval
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Set Notification Interval") },
                    text = {
                        TextField(
                            value = notificationInterval.toString(),
                            onValueChange = { newValue ->
                                if (newValue.isNotEmpty() && newValue.all { it.isDigit() }) {
                                    var newInterval = newValue.toLong()
                                    if (newInterval < 15) {
                                        newInterval = 15
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Notification interval cannot be less than 15 minutes")
                                        }
                                    }
                                    notificationInterval = newInterval
                                    saveSettings() // Save automatically every time the user changes the value
                                    if (isToggled) {
                                        schedulePeriodicNotifications(context, notificationInterval)
                                    }
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("New notification interval: $notificationInterval minutes")
                                    }
                                }
                            },
                            label = { Text("Notification interval (minutes)") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

        }
    }


}
