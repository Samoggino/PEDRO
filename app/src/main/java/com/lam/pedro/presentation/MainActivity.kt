package com.lam.pedro.presentation

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.lam.pedro.presentation.theme.PedroTheme
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.util.notification.schedulePeriodicNotifications

/**
 * The entry point
 */
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
        SecurePreferencesManager.initialize(this)

        val healthConnectManager = (application as BaseApplication).healthConnectManager

        schedulePeriodicNotifications(this, 15)

        setContent {
            PedroTheme {
                PedroApp(healthConnectManager = healthConnectManager)
            }
        }
    }
}