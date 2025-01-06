package com.lam.pedro.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.lam.pedro.R
import kotlinx.coroutines.*

class ActivityTrackingService : Service() {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()

        coroutineScope.launch {
            // Esegui il timer, stepCounter, speedTracker, ecc.
            while (true) {
                delay(1000)
                // Logica per aggiornare il timer, passi, velocità, ecc.
            }
        }

        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "activity_service_channel"
        val channelName = "Activity Tracking"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Activity Tracking")
            .setContentText("Tracking your activity in progress...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Interrompi tutte le coroutine
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

