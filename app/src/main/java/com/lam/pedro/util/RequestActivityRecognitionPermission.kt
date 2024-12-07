package com.lam.pedro.util

import android.Manifest
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/*
@Composable
fun RequestActivityRecognitionPermission() {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.ACTIVITY_RECOGNITION)

    LaunchedEffect(permissionState) {
        // Verifica e richiedi il permesso se non già concesso
        if (permissionState.status.isGranted) {
            Log.d("Permission", "Permesso già concesso")
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.isGranted) {
        // Qui puoi avviare il listener per il sensore
        Log.d("Sensor", "Permesso concesso, avvia il sensore")
    } else {
        // Mostra un messaggio se il permesso non è stato concesso
        Log.d("Permission", "Non è stato concesso il permesso")
    }
}

 */
