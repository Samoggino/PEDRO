package com.lam.pedro.presentation.screen.more.settingsscreen

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.lam.pedro.data.datasource.geofencing.GeofenceManager
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.CustomSnackbarHost
import com.lam.pedro.util.geofence.GeofenceLocationCallback
import com.lam.pedro.util.geofence.GeofencingBroadcastReceiver
import com.lam.pedro.util.geofence.GeofencingService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofencingScreen(onNavBack: () -> Unit, titleId: Int) {

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val geofenceManager = remember { GeofenceManager(context) }

    // Ottieni il ViewModel
    val viewModel: GeofencingViewModel = viewModel(
        factory = GeofencingViewModelFactory(geofenceManager)
    )

    // Stato per il dialogo
    var isDialogOpen by remember { mutableStateOf(false) }

    // Lista delle geofence create
    val geofences by viewModel.geofences

    val REQUIRED_PERMISSIONS = mutableListOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }.toTypedArray()

    var geofencingService: GeofencingService? = null
    var isServiceBound by remember { mutableStateOf(false) }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as GeofencingService.LocalBinder
            geofencingService = binder.getService()
            val locationCallback = object : GeofenceLocationCallback {
                override fun onLocationUpdated(latitude: Double, longitude: Double) {
                    // Logica per gestire l'aggiornamento della posizione
                    Log.d("Geofence", "Location updated: Lat=$latitude, Lng=$longitude")
                }
            }
            geofencingService?.setLocationCallback(locationCallback)

            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            geofencingService = null
            isServiceBound = false
        }
    }

    val requestBackgroundPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.getLocation(context, serviceConnection)
            } else {
                Toast.makeText(context, "Missing permission", Toast.LENGTH_SHORT).show()
            }
        }

    @SuppressLint("MissingPermission")
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        Log.i("isGranted", isGranted.toString())
        if (isGranted.containsValue(false)) {
            Toast.makeText(context, "Missing permission(s)", Toast.LENGTH_SHORT).show()
        } else {
            requestBackgroundPermission.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }





    lateinit var geofencingClient: GeofencingClient

    val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofencingBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                geofencingClient = LocationServices.getGeofencingClient(context)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                geofencingClient.removeGeofences(geofencePendingIntent).run {
                    addOnCompleteListener {

                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                    BackButton(onNavBack)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0f)
                )
            )
        },
        snackbarHost = { CustomSnackbarHost(snackbarHostState) },
        floatingActionButton = {
            // Creiamo un MultiFloatingActionButton con due FAB
            MultiFloatingActionButton(
                onClickPrimary = {
                    Toast.makeText(context, "Please wait 10 seconds...", Toast.LENGTH_SHORT).show()
                    // Avvia il servizio quando il primo bottone viene cliccato
                    val intent = Intent(context, GeofencingService::class.java).apply {
                        action = GeofencingService.ACTION_START
                    }
                    context.startService(intent)
                },
                onClickSecondary = {
                    // Logica per il secondo FAB, ad esempio aprire un dialog
                    isDialogOpen = true
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if (geofences.isEmpty()) {
                Text(
                    text = "No geofences yet",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            LazyColumn() {
                items(geofences) { (name, geofence) ->  // Decomponi la coppia in name e geofence
                    GeofenceItem(
                        name = name,              // Passa il nome
                        geofence = geofence,      // Passa la geofence
                        onRemove = { key -> viewModel.removeGeofence(key) }
                    )
                }

            }
        }

        if (isDialogOpen) {
            AddGeofenceDialog(
                onDismiss = { isDialogOpen = false },
                onConfirm = { name, latitude, longitude, radius ->
                    isDialogOpen = false

                    // Inizializza Location con un provider (ad esempio, "GPS")
                    val location = Location("GPS").apply {
                        this.latitude = latitude
                        this.longitude = longitude
                    }

                    val data = "${location.latitude},${location.longitude},${radius}"

                    viewModel.addGeofence(name, data.hashCode().toString(), location, radius)

                    viewModel.registerGeofence()
                },
                snackbarHostState = snackbarHostState
            )
        }

    }
}


@Composable
fun AddGeofenceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, Float) -> Unit, // Aggiungi il parametro 'name' alla funzione onConfirm
    snackbarHostState: SnackbarHostState
) {
    var name by remember { mutableStateOf("") } // Nuovo stato per il nome della geofence
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) } // Stato per gli errori del nome
    var latitudeError by remember { mutableStateOf(false) }
    var longitudeError by remember { mutableStateOf(false) }
    var radiusError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Geofence")
        },
        text = {
            Column {
                Text("All fields are mandatory")

                // Nome della geofence
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Geofence Name") },
                    isError = nameError,
                    shape = RoundedCornerShape(26.dp)
                )
                if (nameError) {
                    Text(
                        text = "Name cannot be empty",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    isError = latitudeError,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(26.dp)
                )
                if (latitudeError) {
                    Text(
                        text = "Invalid latitude",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    isError = longitudeError,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(26.dp)
                )
                if (longitudeError) {
                    Text(
                        text = "Invalid longitude",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Radius (meters)") },
                    isError = radiusError,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(26.dp)
                )
                if (radiusError) {
                    Text(
                        text = "Invalid radius",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nameValue = name.trim()
                    val latitudeValue = latitude.toDoubleOrNull()
                    val longitudeValue = longitude.toDoubleOrNull()
                    val radiusValue = radius.toFloatOrNull()

                    // Reset degli errori
                    nameError = false
                    latitudeError = false
                    longitudeError = false
                    radiusError = false

                    // Verifica se i valori sono validi
                    if (nameValue.isNotEmpty() && latitudeValue != null && longitudeValue != null && radiusValue != null) {
                        if (latitudeValue in -90.0..90.0 && longitudeValue in -180.0..180.0 && radiusValue > 0) {
                            onConfirm(nameValue, latitudeValue, longitudeValue, radiusValue)
                        } else {
                            if (latitudeValue !in -90.0..90.0) latitudeError = true
                            if (longitudeValue !in -180.0..180.0) longitudeError = true
                            if (radiusValue <= 0) radiusError = true
                        }
                    } else {
                        if (nameValue.isEmpty()) nameError = true
                        if (latitudeValue == null) latitudeError = true
                        if (longitudeValue == null) longitudeError = true
                        if (radiusValue == null) radiusError = true
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun GeofenceItem(name: String, geofence: Geofence, onRemove: (String) -> Unit) {
    Card(modifier = Modifier
        .width(350.dp)
        .padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: $name")
            Text("ID: ${geofence.requestId}")
            Text("Latitude: ${geofence.latitude}")
            Text("Longitude: ${geofence.longitude}")
            Text("Radius: ${geofence.radius}")
            Button(onClick = { onRemove(geofence.requestId) }) {
                Text("Remove")
            }
        }
    }
}

@Composable
fun MultiFloatingActionButton(onClickPrimary: () -> Unit, onClickSecondary: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Secondo FAB (secondario)
        FloatingActionButton(
            onClick = onClickSecondary,
            modifier = Modifier.padding(bottom = 80.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Geofence")
        }

        // Primo FAB (primario)
        FloatingActionButton(
            onClick = onClickPrimary,
            modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Start Service")
        }
    }
}