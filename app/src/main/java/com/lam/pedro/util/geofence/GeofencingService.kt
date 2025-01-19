package com.lam.pedro.util.geofence

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lam.pedro.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeofencingService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var geofeneceLocationClient: GeofenceLocationClient
    private lateinit var locationClient: FusedLocationProviderClient

    private val binder = LocalBinder()
    private var locationCallback: GeofenceLocationCallback? = null

    inner class LocalBinder : Binder() {
        fun getService(): GeofencingService = this@GeofencingService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun setLocationCallback(callback: GeofenceLocationCallback) {
        locationCallback = callback
    }

    override fun onCreate() {
        super.onCreate()
        geofeneceLocationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)

    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.pedro_icon)
            .setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        geofeneceLocationClient
            .getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude
                val long = location.longitude
                Log.i("location231", "lat lng $lat $long")
                locationCallback?.onLocationUpdated(lat, long)

                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $long)"
                )

                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())


    }
    /*
    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")  // Il contenuto iniziale della notifica
            .setSmallIcon(R.drawable.pedro_icon)
            .setOngoing(true)  // La notifica rimarrà persistente
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Avvia il servizio in foreground
        startForeground(1, notification.build())

        // Esegui un'operazione ogni 10 secondi
        serviceScope.launch {
            while (isActive) {
                delay(10000L)  // Attendi 10 secondi
                val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                // Aggiorna la notifica con l'orario corrente
                updateLocation(notificationManager, notification, currentTime)
            }
        }

    }

     */

    @SuppressLint("MissingPermission")
    private suspend fun updateLocation(notificationManager: NotificationManager, notification: NotificationCompat.Builder, currentTime: String) {
        try {
            val location = locationClient.lastLocation.await()  // Ottieni la posizione corrente
            val lat = location?.latitude ?: 0.0
            val long = location?.longitude ?: 0.0
            Log.i("location231", "lat lng $lat $long")

            // Aggiungi il callback per aggiornare la UI (se necessario)
            locationCallback?.onLocationUpdated(lat, long)

            // Aggiorna il contenuto della notifica con le nuove coordinate
            val updatedNotification = notification.setContentText("Location: ($lat, $long) | Time: $currentTime")

            // Aggiorna la notifica con le nuove informazioni
            notificationManager.notify(1, updatedNotification.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}