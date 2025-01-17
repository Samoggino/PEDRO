package com.lam.pedro.presentation.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.ActivityTransitionResult
import com.lam.pedro.data.datasource.activityRecognition.UserActivityTransitionManager.Companion.getActivityType
import com.lam.pedro.data.datasource.activityRecognition.UserActivityTransitionManager.Companion.getTransitionType

@Composable
fun UserActivityBroadcastReceiver(
    systemAction: String,
    systemEvent: (userActivity: String) -> Unit,
) {
    val context = LocalContext.current
    val currentSystemOnEvent by rememberUpdatedState(systemEvent)

    DisposableEffect(context, systemAction) {

        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) {
                    Log.d("UserActivityReceiver", "Received null intent")
                    return
                }

                Log.d("UserActivityReceiver", "Received intent: $intent")

                val result = ActivityTransitionResult.extractResult(intent)
                if (result == null) {
                    Log.d("UserActivityReceiver", "No transition result found in the intent")
                    return
                }

                var resultStr = ""
                for (event in result.transitionEvents) {
                    resultStr += "${getActivityType(event.activityType)} - ${getTransitionType(event.transitionType)}\n"
                }

                Log.d("UserActivityReceiver", "Transition Events: $resultStr")
                currentSystemOnEvent(resultStr)
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("UserActivityReceiver", "Registering BroadcastReceiver (TIRAMISU+) for action: $systemAction")
            context.registerReceiver(broadcast, intentFilter, RECEIVER_NOT_EXPORTED)
            Log.d("UserActivityReceiver", "Registered BroadcastReceiver (TIRAMISU+) for action: $systemAction")
        } else {
            Log.d("UserActivityReceiver", "Registering BroadcastReceiver for action: $systemAction")
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(broadcast, intentFilter)
        }

        onDispose {
            Log.d("UserActivityReceiver", "Unregistering BroadcastReceiver for action: $systemAction")
            context.unregisterReceiver(broadcast)
        }
    }
}