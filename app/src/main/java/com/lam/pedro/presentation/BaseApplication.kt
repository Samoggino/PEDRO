package com.lam.pedro.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.lam.pedro.BuildConfig
import com.lam.pedro.data.HealthConnectManager
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class BaseApplication : Application() {
    val healthConnectManager by lazy {
        HealthConnectManager(this)
    }

    override fun onCreate() {
        super.onCreate()

        //val channel = NotificationChannel("location", "Location", NotificationManager.IMPORTANCE_LOW)
        val channelId = "Location"
        val name = "Location Notifications"
        val descriptionText = "Notifications for geofence transitions"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        //val notificationManager= getSystemService(NotificationManager::class.java)
        //notificationManager.createNotificationChannel(channel)

        // Inizializza MapLibre
        MapLibre.getInstance(
            this,
            BuildConfig.MAPLIBRE_ACCESS_TOKEN, // Sostituisci con una chiave API valida oppure usa null
            WellKnownTileServer.MapTiler // Usa il tile server appropriato
        )
    }
}

