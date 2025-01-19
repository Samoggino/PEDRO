package com.lam.pedro.util.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.lam.pedro.R

class GeofencingBroadcastReceiver : BroadcastReceiver() {
    @Suppress("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) }
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeofencingBroadcastReceiver", "onReceive: $errorMessage")
            return
        }

        when (
            val geofenceTransition = geofencingEvent?.geofenceTransition) {

            Geofence.GEOFENCE_TRANSITION_ENTER -> {


                val notificationBuilder = context?.let {
                    NotificationCompat.Builder(it, "location")
                        .setSmallIcon(R.drawable.pedro_icon)
                        .setContentTitle("Geofence Alert")
                        .setContentText("Entered geofence")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                }


                val notificationManager = context?.let { NotificationManagerCompat.from(it) }
                notificationBuilder?.build()?.let { notificationManager?.notify(1, it) }


            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                val notificationBuilder = context?.let {
                    NotificationCompat.Builder(it, "location")
                        .setSmallIcon(R.drawable.pedro_icon)
                        .setContentTitle("Geofence Alert")
                        .setContentText("Entered geofence")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                }
                val notificationManager = context?.let { NotificationManagerCompat.from(it) }
                notificationBuilder?.build()?.let { notificationManager?.notify(1, it) }
            }

            else -> {
                Log.e("GeofencingBroadcastReceiver", "onReceive: ${geofenceTransition.toString()}")
            }
        }
    }


}