package com.lam.pedro.presentation.screen.more.settingsscreen

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.lam.pedro.data.datasource.geofencing.GeofenceManager
import com.lam.pedro.util.geofence.GeofencingService
import kotlinx.coroutines.launch

/*
class GeofencingViewModel(
    private val geofencingClient: GeofencingClient
) : ViewModel() {

    val geofences: LiveData<List<Geofence>> = liveData {
        // Recupera le geofence dal GeofencingClient (se questa funzionalità è disponibile)
        val geofencesList = geofencingClient.getGeofences() // esempio, potrebbe variare a seconda delle API
        emit(geofencesList)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(context: Context, latitude: Double, longitude: Double, radius: Float) {
        val geofence = Geofence.Builder()
            .setRequestId("Geofence_${System.currentTimeMillis()}")
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
            .addOnSuccessListener {
                Log.d("Geofence", "Geofence added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Geofence", "Failed to add geofence", e)
            }
    }
}

 */

class GeofencingViewModel(
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    // Lista delle geofence salvate
    private val _geofences = mutableStateOf<List<Pair<String, Geofence>>>(emptyList())
    val geofences: State<List<Pair<String, Geofence>>> = _geofences

    init {
        getGeofences()
    }

    // Salva una nuova geofence
    fun addGeofence(name: String, key: String, location: Location, radiusInMeters: Float = 100.0f) {
        geofenceManager.addGeofence(name, key, location, radiusInMeters)
        _geofences.value = geofenceManager.getSavedLocations()
    }


    // Recupera tutte le geofence salvate
    private fun getGeofences() {
        _geofences.value = geofenceManager.getSavedLocations()
    }

    // Elimina una geofence
    fun removeGeofence(key: String) {
        geofenceManager.removeGeofence(key)
        _geofences.value = geofenceManager.getSavedLocations()
    }

    // Registra le geofence
    fun registerGeofence() {
        geofenceManager.registerGeofence()
    }

    // Deregistra tutte le geofence
    fun deregisterGeofence() {
        viewModelScope.launch {
            geofenceManager.deregisterGeofence()
        }
    }

    fun deregisterGeofenceForKey(key: String) {
        viewModelScope.launch {
            geofenceManager.deregisterGeofenceForKey(key)
        }
    }

    fun getLocation(context: Context, serviceConnection: ServiceConnection) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        if (isEnabled) {
            val serviceIntent = Intent(context, GeofencingService::class.java).apply {
                action = GeofencingService.ACTION_START
            }
            context.startService(serviceIntent)
            context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
            Toast.makeText(context, "Please enable location services", Toast.LENGTH_SHORT).show()

        }
    }
}




class GeofencingViewModelFactory(
    private val geofencingManager: GeofenceManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GeofencingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GeofencingViewModel(geofencingManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}