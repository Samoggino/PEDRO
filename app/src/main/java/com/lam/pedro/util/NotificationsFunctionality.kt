package com.lam.pedro.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.lam.pedro.R
import com.lam.pedro.presentation.component.DeniedPermissionDialog
import com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen.ScreenFunctionality

class NotificationsFunctionality(private val context: Context) : ScreenFunctionality {

    private var hasNotificationPermission by mutableStateOf(false)
    private var showNotificationPermissionDialog by mutableStateOf(false)
    private var requestNotificationPermissionCounter by mutableIntStateOf(0)
    private var hasBeenAskedForNotificationPermission by mutableStateOf(false)

    // Funzione per verificare i permessi all'avvio dello screen
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasNotificationPermission = true
            showNotificationPermissionDialog = false
        } else {
            if (hasBeenAskedForNotificationPermission)
                showNotificationPermissionDialog = true
        }
    }

    @Composable
    override fun Execute() {
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current as Activity

        // Launcher per richiedere il permesso di POST_NOTIFICATIONS
        val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                Log.d(TAG, "-----------------Notification Permission granted-----------------")
            } else {
                Log.d(TAG, "-----------------Notification Permission denied-----------------")
                hasBeenAskedForNotificationPermission = true
                requestNotificationPermissionCounter++
                showNotificationPermissionDialog = true
            }
        }

        checkPermissions()

        // Controllo dei permessi
        var hasNotificationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        LaunchedEffect(hasNotificationPermission) {
            if (!hasNotificationPermission && !hasBeenAskedForNotificationPermission) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val updatedPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    hasNotificationPermission = updatedPermission
                    if (updatedPermission) {
                        showNotificationPermissionDialog = false
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        // Mostriamo il dialog per il permesso di notifiche
        DeniedPermissionDialog(
            showDialog = showNotificationPermissionDialog,
            onDismiss = {
                if (hasNotificationPermission) {
                    showNotificationPermissionDialog = false
                }
            },
            onGoToSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            },
            color = MaterialTheme.colorScheme.primary,
            title = R.string.notification_permission_title,
            icon = R.drawable.notification_icon,
            text = R.string.notification_permission_description,
            buttonText = R.string.go_to_settings
        )
    }


    private fun launchPermissionRequest(context: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Richiedi il permesso di notifiche
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        } else {
            // Le notifiche non richiedono permessi su versioni precedenti
            Log.d(
                "NotificationPermission",
                "POST_NOTIFICATIONS non richiesto per questa versione di Android"
            )
        }
    }
}
