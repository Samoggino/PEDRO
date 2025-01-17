package com.lam.pedro.presentation.screen.more.settingsscreen

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import com.lam.pedro.presentation.component.PermissionBox
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lam.pedro.data.datasource.geofencing.GeofenceManager
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.CustomSnackbarHost
import com.lam.pedro.util.CUSTOM_INTENT_GEOFENCE
import com.lam.pedro.util.services.GeofenceBroadcastReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofencingScreen(navController: NavHostController, titleId: Int) {

    val snackbarHostState = remember { SnackbarHostState() }

    val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    // Stato per i luoghi salvati
    val savedLocations = remember { mutableStateOf<List<Pair<String, Location>>>(emptyList()) }

    // Logica per caricare i luoghi salvati
    val context = LocalContext.current
    val geofenceManager = remember { GeofenceManager(context) }
    LaunchedEffect(Unit) {
        savedLocations.value = geofenceManager.getSavedLocations()
    }

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
    ) {

        // Requires at least coarse permission
        PermissionBox(
            permissions = permissions,
            requiredPermissions = listOf(permissions.first()),
        ) {
            // For Android 10 onwards, we need background permission
            PermissionBox(
                permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            ) {
                // UI del geofencing
                GeofencingControls(geofenceManager)

                // UI dei luoghi salvati
                if (savedLocations.value.isEmpty()) {
                    Text(
                        text = "Nessun luogo salvato",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(savedLocations.value) { (key, location) ->
                            LocationItem(key = key, location = location)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GeofencingControls(geofenceManager: GeofenceManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var geofenceTransitionEventInfo by remember { mutableStateOf("") }

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            scope.launch(Dispatchers.IO) {
                geofenceManager.deregisterGeofence()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GeofenceList(geofenceManager)
        Button(
            onClick = {
                if (geofenceManager.geofenceList.isNotEmpty()) {
                    geofenceManager.registerGeofence()
                } else {
                    Toast.makeText(
                        context,
                        "Please add at least one geofence to monitor",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
        ) {
            Text(text = "Register Geofences")
        }

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    geofenceManager.deregisterGeofence()
                }
            },
        ) {
            Text(text = "Deregister Geofences")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = geofenceTransitionEventInfo)
    }
}

@Composable
fun GeofenceList(geofenceManager: GeofenceManager) {
    val checkedGeoFence1 = remember { mutableStateOf(false) }
    val checkedGeoFence2 = remember { mutableStateOf(false) }
    val checkedGeoFence3 = remember { mutableStateOf(false) }

    Text(text = "Available Geofence")
    Row(
        Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checkedGeoFence1.value,
            onCheckedChange = { checked ->
                if (checked) {
                    geofenceManager.addGeofence(
                        "statue_of_liberty",
                        location = Location("").apply {
                            latitude = 40.689403968838015
                            longitude = -74.04453795094359
                        },
                    )
                } else {
                    geofenceManager.removeGeofence("statue_of_libery")
                }
                checkedGeoFence1.value = checked
            },
        )
        Text(text = "Statue of Liberty")
    }
    Row(
        Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checkedGeoFence2.value,
            onCheckedChange = { checked ->
                if (checked) {
                    geofenceManager.addGeofence(
                        "eiffel_tower",
                        location = Location("").apply {
                            latitude = 48.85850
                            longitude = 2.29455
                        },
                    )
                } else {
                    geofenceManager.removeGeofence("eiffel_tower")
                }
                checkedGeoFence2.value = checked
            },
        )
        Text(text = "Eiffel Tower")
    }
    Row(
        Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checkedGeoFence3.value,
            onCheckedChange = { checked ->
                if (checked) {
                    geofenceManager.addGeofence(
                        "vatican_city",
                        location = Location("").apply {
                            latitude = 41.90238
                            longitude = 12.45398
                        },
                    )
                } else {
                    geofenceManager.removeGeofence("vatican_city")
                }
                checkedGeoFence3.value = checked
            },
        )
        Text(text = "Vatican City")
    }
}

@Composable
fun LocationItem(key: String, location: Location) {
    // Componente per visualizzare ogni luogo in una card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Nome: $key", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Latitudine: ${location.latitude}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Longitudine: ${location.longitude}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
