package com.lam.pedro.presentation.screen.more.settingsscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.Geofence
import com.lam.pedro.data.datasource.geofencing.GeofenceManager
import com.lam.pedro.presentation.component.BackButton
import com.lam.pedro.presentation.component.CustomSnackbarHost
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.width

/*
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofencingScreen(onNavBack: () -> Unit, titleId: Int) {

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
                        BackButton(onNavBack)
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

 */


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
            FloatingActionButton(onClick = { isDialogOpen = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Geofence")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if (geofences.isEmpty()) {
                Text(
                    text = "No geofences yet",
                    style = MaterialTheme.typography.headlineSmall
                )
            } else {
                Text(
                    text = "Geofences",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            LazyColumn {
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
                onConfirm = {name, latitude, longitude, radius ->
                    isDialogOpen = false

                    // Inizializza Location con un provider (ad esempio, "GPS")
                    val location = Location("GPS").apply {
                        this.latitude = latitude
                        this.longitude = longitude
                    }

                    val data = "${location.latitude},${location.longitude},${radius}"

                    viewModel.addGeofence(name, data.hashCode().toString(), location, radius)
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
    Card(modifier = Modifier.width(350.dp).padding(8.dp)) {
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

