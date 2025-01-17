package com.lam.pedro.util.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.lam.pedro.R
import com.lam.pedro.data.datasource.activityRecognition.UserActivityTransitionManager
import com.lam.pedro.presentation.MainActivity
import com.lam.pedro.presentation.TAG
import com.lam.pedro.util.CUSTOM_INTENT_GEOFENCE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
class GeofencingService(systemEvent: (userActivity: String) -> Unit) : Service() {

    private lateinit var manager: UserActivityTransitionManager
    private lateinit var receiver: BroadcastReceiver
    private lateinit var handler: Handler
    private lateinit var updateRunnable: Runnable
    val currentSystemOnEvent by rememberUpdatedState(systemEvent)

    private var currentNotificationText: String = "Geofencing service is running..."

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        manager = UserActivityTransitionManager(this)

        // Inizializza il Handler per aggiornare la notifica ogni 10 secondi
        handler = Handler(Looper.getMainLooper())
        updateRunnable = Runnable {
            // Log per indicare che sta avvenendo un aggiornamento programmato
            Log.d("ActivityRecognitionService", "Nessun intent ricevuto. Aggiornamento notifica...")
            // Aggiorna la notifica utilizzando il testo corrente
            updateNotification(currentNotificationText)
            handler.postDelayed(
                updateRunnable,
                10000
            ) // Riprogramma l'aggiornamento dopo 10 secondi
        }

        // Avvia il ciclo di aggiornamenti ogni 10 secondi
        handler.postDelayed(updateRunnable, 10000) // Prima esecuzione dopo 10 secondi

        // Configurazione del BroadcastReceiver per aggiornare la notifica
        val intentFilter = IntentFilter(CUSTOM_INTENT_GEOFENCE)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) } ?: return

                if (geofencingEvent.hasError()) {
                    val errorMessage =
                        GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                    Log.e(TAG, "onReceive: $errorMessage")
                    return
                }
                val alertString = "Geofence Alert :" +
                        " Trigger ${geofencingEvent.triggeringGeofences}" +
                        " Transition ${geofencingEvent.geofenceTransition}"
                Log.d(
                    TAG,
                    alertString
                )
                currentSystemOnEvent(alertString)
            }
        }
        context.registerReceiver(broadcast, intentFilter, )
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(contentText: String): Notification {
        val notificationChannelId = "activity_recognition_channel"

        // Crea il Notification Channel per Android 8.0+
        val channel = NotificationChannel(
            notificationChannelId,
            "Activity Recognition",
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)


        // Intent per riaprire l'app
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // Aggiungi il contentText dinamico
        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Activity Recognition Service")
            .setContentText(contentText)  // Usa il contentText dinamico
            .setSmallIcon(R.drawable.pedro_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)  // Imposta la notifica come in corso e non scartabile
            .setAutoCancel(false)  // Impedisce la cancellazione automatica della notifica
            .setContentIntent(pendingIntent) // Consenti di cliccare sulla notifica per aprire l'app
            .build()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Verifica del permesso ACTIVITY_RECOGNITION
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Avvia il monitoraggio delle transizioni
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    manager.registerActivityTransitions()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    stopSelf() // Interrompe il servizio se non può funzionare
                }
            }

            // Avvia il servizio in foreground con una notifica iniziale
            startForeground(
                NOTIFICATION_ID,
                createNotification("Monitoring activity transitions...")
            )
        } else {
            // Permesso non concesso: ferma il servizio
            stopSelf()
        }

        return START_STICKY
    }

    // Quando vuoi fermare il servizio e rimuovere la notifica
    override fun onDestroy() {
        super.onDestroy()

        isServiceRunning = false

        handler.removeCallbacks(updateRunnable)

        unregisterReceiver(receiver)

        // Interrompi il servizio in foreground e rimuovi la notifica
        stopForeground(STOP_FOREGROUND_REMOVE) // Imposta la notifica come non visibile
        stopSelf() // Ferma il servizio
    }

    private fun updateNotification(contentText: String) {
        val timestamp = System.currentTimeMillis()
        val timestampFormatted =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
        val debugText = "$contentText Updated at: $timestampFormatted"

        Log.d("UPDATE_NOTIFICATION", debugText)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification(debugText))
    }

    companion object {
        const val NOTIFICATION_ID = 3
        var isServiceRunning = false
    }
}

 */