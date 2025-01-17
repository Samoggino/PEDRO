package com.lam.pedro.util.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.lam.pedro.R
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.positions
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.speedCounter
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.totalSpeed
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.averageSpeed
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.speedSamples
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.exerciseRoute
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.distance
import com.lam.pedro.data.activityTrackingRepository.ActivityTrackingRepository.steps
import com.lam.pedro.util.LocationTracker
import com.lam.pedro.util.SpeedTracker
import com.lam.pedro.util.StepCounter
import com.lam.pedro.util.updateDistance
import kotlinx.coroutines.*
import org.maplibre.android.geometry.LatLng

class ActivityTrackingService : Service() {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var speedTracker: SpeedTracker
    private lateinit var locationTracker: LocationTracker
    private lateinit var stepCounter: StepCounter

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()

        coroutineScope.launch {
            launch { startSpeedTracking() }
            launch { startLocationTracking() }
            launch { startStepCounter() }
        }

        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "activity_service_channel"
        val channelName = "Activity Tracking"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
                Log.d("ActivityTrackingService", "Notification channel created.")
            } else {
                Log.e("ActivityTrackingService", "NotificationManager is null")
            }
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Activity Tracking")
            .setContentText("Tracking your activity in progress...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        Log.d("ActivityTrackingService", "Starting foreground service...")
        startForeground(1, notification)
        Log.d("ActivityTrackingService", "Foreground service started")
    }


    private suspend fun startSpeedTracking() {
        speedTracker.trackSpeed().collect { sample ->
            speedCounter.intValue++
            totalSpeed.value += sample.speed.inMetersPerSecond
            averageSpeed.doubleValue =
                totalSpeed.doubleValue / speedCounter.intValue
            speedSamples.add(sample)
            Log.d(com.lam.pedro.presentation.TAG, "----------------------New Speed Sample: $sample")
        }
    }

    private suspend fun startLocationTracking() {
        locationTracker.trackLocation().collect { location ->
            exerciseRoute.add(location)
            Log.d(com.lam.pedro.presentation.TAG, "--------------------------------New location: $location")
            val newLatLng = LatLng(location.latitude, location.longitude)
            updateDistance(distance, positions, newLatLng)
            positions.add(newLatLng)
            Log.d(
                com.lam.pedro.presentation.TAG,
                "--------------------------------New distance: ${distance.doubleValue}"
            )
        }
    }

    private suspend fun startStepCounter() {
        try {
            stepCounter.isAvailable()
            stepCounter.stepsCounter { newSteps ->
                steps.floatValue = newSteps // Aggiorna lo stato della UI
            }
            //steps = stepCount.toFloat() // Aggiorna lo stato
            Log.d("STEP_COUNTER", "Steps: $steps")
        } catch (e: Exception) {
            Log.e("STEP_COUNTER", "Error retrieving steps: ${e.message}")
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Inizializza gli oggetti usando il contesto del Service
        speedTracker = SpeedTracker(applicationContext)
        locationTracker = LocationTracker(applicationContext)
        stepCounter = StepCounter(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Interrompi tutte le coroutine
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
