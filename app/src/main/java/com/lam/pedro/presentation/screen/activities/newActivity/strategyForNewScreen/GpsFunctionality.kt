package com.lam.pedro.presentation.screen.activities.newActivity.strategyForNewScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.lam.pedro.presentation.TAG
import com.lam.pedro.presentation.component.DeniedPermissionDialog
import com.lam.pedro.R

class GpsFunctionality(private val context: Context) : ScreenFunctionality {

    private var hasLocationPermission by mutableStateOf(false)
    private var showLocationPermissionDialog by mutableStateOf(false)
    private var requestLocationPermissionCounter by mutableIntStateOf(0)
    private var hasBeenAskedForLocationPermission by mutableStateOf(false)

    // Funzione per verificare i permessi all'avvio dello screen
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasLocationPermission = true
            showLocationPermissionDialog = false
        } else {
            if (hasBeenAskedForLocationPermission)
                showLocationPermissionDialog = true
        }
    }

    @Composable
    override fun Execute() {
        val lifecycleOwner = LocalLifecycleOwner.current

        // Launcher per richiedere il permesso di ACCESS_FINE_LOCATION
        val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                Log.d(TAG, "-----------------GPS Permission granted-----------------")
            } else {
                Log.d(TAG, "-----------------GPS Permission denied-----------------")
                hasBeenAskedForLocationPermission = true
                requestLocationPermissionCounter++
                showLocationPermissionDialog = true
            }
        }

        checkPermissions()

        // Avvia la richiesta del permesso solo se non è stato ancora concesso
        if (!hasLocationPermission) {
            LaunchedEffect(Unit) {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        // Controlla se i permessi sono concessi quando lo screen torna attivo
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            hasLocationPermission = true
                            showLocationPermissionDialog = false
                        } else {
                            if (hasBeenAskedForLocationPermission)
                                showLocationPermissionDialog = true
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
            showDialog = showLocationPermissionDialog,
            onDismiss = {
                if (hasLocationPermission) {
                    showLocationPermissionDialog = false
                }
            },
            onGoToSettings = {
                if (requestLocationPermissionCounter < 2) {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    // Vai alle impostazioni dell'app
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }
            },
            color = MaterialTheme.colorScheme.primary,
            title = R.string.location_permission_title,
            icon = R.drawable.location_icon,
            text = R.string.location_permission_description,
            buttonText = if (requestLocationPermissionCounter < 2) R.string.request_permission else R.string.go_to_settings
        )
    }

}

