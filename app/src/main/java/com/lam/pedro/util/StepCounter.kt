package com.lam.pedro.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val TAG = "STEP_COUNT_LISTENER"

class StepCounter(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    fun isAvailable() {
        val isStepCounterAvailable = sensor != null
        if (!isStepCounterAvailable) {
            Log.d(TAG, "---------------------------TYPE_STEP_COUNTER NOT AVAILABLE---------------------------")
            return
        } else {
            Log.d(TAG, "---------------------------TYPE_STEP_COUNTER AVAILABLE---------------------------")
        }
    }

    /*
    suspend fun steps() = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "Registering sensor listener... ")

        val listener: SensorEventListener by lazy {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return

                    val stepsSinceLastReboot = event.values[0].toLong()
                    Log.d(TAG, "Steps since last reboot: $stepsSinceLastReboot")

                    if (continuation.isActive) {
                        continuation.resume(stepsSinceLastReboot)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    Log.d(TAG, "Accuracy changed to: $accuracy")
                }
            }
        }

        val supportedAndEnabled = sensorManager.registerListener(
            listener,
            sensor, SensorManager.SENSOR_DELAY_UI
        )
        Log.d(TAG, "Sensor listener registered: $supportedAndEnabled")
    }
     */

    suspend fun stepsCounter(updateSteps: (Float) -> Unit) = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "Registering sensor listener... ")

        var initialSteps: Long? = null // Valore iniziale da cui calcolare la differenza

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val stepsSinceLastReboot = event.values[0].toLong()

                if (initialSteps == null) {
                    // Memorizza il valore iniziale dei passi
                    initialSteps = stepsSinceLastReboot
                    Log.d(TAG, "Initial steps set to: $initialSteps")
                } else {
                    // Calcola i passi rispetto al valore iniziale
                    val stepsSinceListenerStarted = stepsSinceLastReboot - initialSteps!!
                    Log.d(TAG, "Steps since listener started: $stepsSinceListenerStarted")
                    updateSteps(stepsSinceListenerStarted.toFloat())

                    if (continuation.isActive) {
                        continuation.resume(stepsSinceListenerStarted)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(TAG, "Accuracy changed to: $accuracy")
            }
        }

        // Registra il listener
        val supportedAndEnabled = sensorManager.registerListener(
            listener,
            sensor,
            SensorManager.SENSOR_DELAY_UI
        )

        if (!supportedAndEnabled) {
            Log.d(TAG, "Failed to register sensor listener")
            continuation.resumeWith(Result.failure(Exception("Failed to register sensor listener")))
        }

        // Cancella il listener se il coroutine viene cancellato
        continuation.invokeOnCancellation {
            Log.d(TAG, "Unregistering sensor listener...")
            sensorManager.unregisterListener(listener)
        }
    }


}