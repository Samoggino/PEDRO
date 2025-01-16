package com.lam.pedro.presentation

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lam.pedro.data.datasource.SecurePreferencesManager
import com.lam.pedro.presentation.onboarding.OnboardingScreen
import com.lam.pedro.presentation.onboarding.OnboardingUtils
import com.lam.pedro.presentation.screen.profile.ProfileViewModel
import com.lam.pedro.presentation.screen.profile.ProfileViewModelFactory
import com.lam.pedro.presentation.theme.PedroTheme
import com.lam.pedro.util.notification.schedulePeriodicNotifications
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val onboardingUtils by lazy { OnboardingUtils(this) }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
        SecurePreferencesManager.initialize(this)
        schedulePeriodicNotifications(this, 15)

        setContent {
            PedroTheme {
                ShowOnboardingScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    private fun ShowOnboardingScreen() {
        val scope = rememberCoroutineScope()
        val profileViewModel : ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current))
        OnboardingScreen(profileViewModel) {
            onboardingUtils.setOnboardingCompleted()
            scope.launch {
                setContent {
                    val healthConnectManager = (application as BaseApplication).healthConnectManager
                    PedroApp(healthConnectManager = healthConnectManager, profileViewModel = profileViewModel)
                }
            }
        }

    }
}