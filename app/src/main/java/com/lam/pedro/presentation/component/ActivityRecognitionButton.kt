package com.lam.pedro.presentation.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lam.pedro.R
import com.lam.pedro.util.services.ActivityRecognitionService

@Composable
fun ActivityRecognitionButton(context: Context) {
    // Stato che tiene traccia se il servizio è attivo o meno
    var isServiceRunning by remember { mutableStateOf(ActivityRecognitionService.isServiceRunning) }

    // Stato che determina il colore dell'immagine (verde se attivo, rosso se non attivo)
    val imageColor = if (isServiceRunning) Color(0xff71c97b) else Color(0xFFf87757)

    // Funzione per avviare o fermare il servizio
    fun toggleService() {
        if (isServiceRunning) {
            // Se il servizio è attivo, fermalo
            val stopServiceIntent = Intent(context, ActivityRecognitionService::class.java)
            context.stopService(stopServiceIntent)
            isServiceRunning = false
        } else {
            // Se il servizio non è attivo, richiedi il permesso e avvialo
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                context.startForegroundService(
                    Intent(
                        context,
                        ActivityRecognitionService::class.java
                    )
                )
            } else {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    1002
                )
            }
            isServiceRunning = true
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Immagine che cambia in base allo stato del servizio
        val imageRes = if (isServiceRunning) R.drawable.check_icon else R.drawable.uncheck_icon

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp),
            colorFilter = ColorFilter.tint(
                imageColor
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Bottone che avvia o ferma il servizio in base allo stato
        Button(onClick = { toggleService() }) {
            Text(text = if (isServiceRunning) "Stop" else "Start")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}