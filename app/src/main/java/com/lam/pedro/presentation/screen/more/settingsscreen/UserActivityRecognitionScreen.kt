package com.lam.pedro.presentation.screen.more.settingsscreen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.lam.pedro.data.datasource.activityRecognition.UserActivityTransitionManager
import com.lam.pedro.presentation.component.ActivityRecognitionButton
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.CustomSnackbarHost
import com.lam.pedro.presentation.component.PermissionBox
import com.lam.pedro.presentation.component.UserActivityBroadcastReceiver
import com.lam.pedro.util.createAndSendTestIntent
import com.lam.pedro.util.services.ActivityRecognitionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun UserActivityRecognitionScreen(navController: NavHostController, titleId: Int) {
    val activityPermission =
        Manifest.permission.ACTIVITY_RECOGNITION

    PermissionBox(permissions = listOf(activityPermission)) {
        UserActivityRecognitionContent(navController, titleId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("InlinedApi")
@RequiresPermission(
    anyOf = [
        Manifest.permission.ACTIVITY_RECOGNITION,
        "com.google.android.gms.permission.ACTIVITY_RECOGNITION",
    ],
)
@Composable
fun UserActivityRecognitionContent(navController: NavHostController, titleId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val manager = remember {
        UserActivityTransitionManager(context)
    }
    var currentUserActivity by remember {
        mutableStateOf("Unknown")
    }
    val transitionHistory = remember { mutableStateListOf<String>() }

    // Calling deregister on dispose
    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            scope.launch(Dispatchers.IO) {
                manager.deregisterActivityTransitions()
            }
        }
    }

    // Register a local broadcast to receive activity transition updates
    UserActivityBroadcastReceiver(systemAction = "USER-ACTIVITY-DETECTION-INTENT-ACTION") { userActivity ->
        currentUserActivity = userActivity
        transitionHistory.add(userActivity)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(titleId),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    BackButton(navController)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            ActivityRecognitionButton(context)

            Button(onClick = { createAndSendTestIntent(context) }) {
                Text(text = "Test")
            }

            if (currentUserActivity.isNotBlank()) {
                Text(
                    text = "Current Activity: $currentUserActivity",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Activity transition updates not registered",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider()
            Text(
                text = "Transition History:",
                style = MaterialTheme.typography.bodyLarge
            )
            transitionHistory.forEach { transition ->
                Text(
                    text = "- $transition",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

