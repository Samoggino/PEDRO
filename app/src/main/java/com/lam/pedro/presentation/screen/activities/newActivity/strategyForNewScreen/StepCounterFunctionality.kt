package com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.lam.pedro.R
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.DeniedPermissionDialog

class StepCounterFunctionality(private val context: Context) : ScreenFunctionality {

    private var hasActivityRecognitionPermission by mutableStateOf(false)
    private var showActivityRecognitionPermissionDialog by mutableStateOf(false)
    private var hasBeenAskedForActivityRecognitionPermission by mutableStateOf(false)

    // Funzione per verificare i permessi all'avvio dello screen
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasActivityRecognitionPermission = true
            showActivityRecognitionPermissionDialog = false
        } else {
            if (hasBeenAskedForActivityRecognitionPermission)
                showActivityRecognitionPermissionDialog = true
        }
    }

    @Composable
    override fun Execute() {
        val lifecycleOwner = LocalLifecycleOwner.current

        // Launcher per richiedere il permesso di ACCESS_FINE_LOCATION
        val requestActivityRecognitionPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasActivityRecognitionPermission = isGranted
            if (isGranted) {
                Log.d(
                    TAG,
                    "-----------------Activity Recognition Permission granted-----------------"
                )
            } else {
                //TODO: Handle permission denied, inform the user
                Log.d(
                    TAG,
                    "-----------------Activity Recognition Permission denied-----------------"
                )
                hasBeenAskedForActivityRecognitionPermission = true
                showActivityRecognitionPermissionDialog = true
            }
        }

        checkPermissions()

        // Avvia la richiesta del permesso solo se non è stato ancora concesso
        if (!hasActivityRecognitionPermission) {
            LaunchedEffect(Unit) {
                requestActivityRecognitionPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        // Controlla se il permesso di ACTIVITY_RECOGNITION è stato concesso
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACTIVITY_RECOGNITION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            hasActivityRecognitionPermission = true
                            showActivityRecognitionPermissionDialog = false
                        } else {
                            // Mostra il dialog se il permesso non è stato concesso
                            if (hasBeenAskedForActivityRecognitionPermission)
                                showActivityRecognitionPermissionDialog = true
                        }
                    }

                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        // Mostriamo i dialog per il permesso di localizzazione
        DeniedPermissionDialog(
            showDialog = showActivityRecognitionPermissionDialog,
            onDismiss = {
                if (hasActivityRecognitionPermission) {
                    showActivityRecognitionPermissionDialog = false
                }
            },
            onGoToSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            },
            color = MaterialTheme.colorScheme.primary,
            title = R.string.activity_recognition_permission_title,
            icon = R.drawable.steps_icon,
            text = if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            ) {
                R.string.activity_recognition_permission_permanently_denied_description
            } else {
                R.string.activity_recognition_permission_description
            },
            buttonText = if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            ) {
                R.string.go_to_settings
            } else {
                R.string.request_permission
            }
        )
    }
}
