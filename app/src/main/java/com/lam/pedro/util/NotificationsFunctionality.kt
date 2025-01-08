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
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    private var requestNotificationPermissionCounter by mutableIntStateOf(0)
    private var hasBeenAskedForNotificationPermission by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    override fun Execute() {
        var showNotificationPermissionDialog by remember { mutableStateOf(false) }
        val lifecycleOwner = LocalLifecycleOwner.current


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

        LaunchedEffect(hasNotificationPermission) {
            if (!hasNotificationPermission && !hasBeenAskedForNotificationPermission) {
                hasBeenAskedForNotificationPermission = true
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (!hasNotificationPermission) {
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when {
                        // 1. Permesso già concesso
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                            hasNotificationPermission = true
                        }

                        // 2. L'utente ha selezionato "Don't ask again"
                        !ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.POST_NOTIFICATIONS) -> {
                            Log.d(TAG, "-----------------Permission denied permanently-----------------")
                            // Mostra il dialog per andare alle impostazioni
                            showNotificationPermissionDialog = true
                        }

                        // 3. L'utente può ancora ricevere richieste di permesso
                        else -> {
                            Log.d(TAG, "-----------------Requesting permission-----------------")
                            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

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
                    if (hasNotificationPermission) {
                        showNotificationPermissionDialog = false
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        DeniedPermissionDialog(
            showDialog = showNotificationPermissionDialog,
            onDismiss = {
                showNotificationPermissionDialog = false
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
            text = if (!ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.POST_NOTIFICATIONS)) {
                R.string.notification_permission_permanently_denied_description
            } else {
                R.string.notification_permission_description
            },
            buttonText = if (!ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.POST_NOTIFICATIONS)) {
                R.string.go_to_settings
            } else {
                R.string.request_permission
            }
        )

    }


}
