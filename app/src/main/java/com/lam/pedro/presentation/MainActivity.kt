package com.lam.pedro.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lam.pedro.presentation.theme.PedroTheme

/**
 * The entry point
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)

        val healthConnectManager = (application as BaseApplication).healthConnectManager

        setContent {
            PedroTheme {
                PedroApp(healthConnectManager = healthConnectManager)
            }
        }
    }
}