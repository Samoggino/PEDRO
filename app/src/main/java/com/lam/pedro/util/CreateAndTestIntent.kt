package com.lam.pedro.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

// Simula un Intent con ActivityTransitionResult
fun createAndSendTestIntent(context: Context) {
    // Dati simulati
    val activityType = DetectedActivity.WALKING // Tipo di attività simulata
    val transitionType = ActivityTransition.ACTIVITY_TRANSITION_ENTER // Tipo di transizione simulata
    val eventTimestamp = System.currentTimeMillis()

    // Crea un evento di transizione
    //val transitionEvent = ActivityTransitionEvent(activityType, transitionType, eventTimestamp)

    // Crea un risultato di transizione con una lista di eventi
    //val transitionResult = ActivityTransitionResult(listOf(transitionEvent))

    // Crea un Intent e aggiungi il risultato come extra
    val testIntent = Intent("USER-ACTIVITY-DETECTION-INTENT-ACTION")
    //testIntent.putExtra("com.google.android.gms.location.ACTIVITY_TRANSITION_RESULT", transitionResult)
    testIntent.putExtra("activityType", activityType)
    testIntent.putExtra("transitionType", transitionType)
    testIntent.putExtra("timestamp", eventTimestamp)

    // Manda il broadcast
    context.sendBroadcast(testIntent)
}
