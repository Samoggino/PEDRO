package com.lam.pedro.data.datasource.geofencing

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.lam.pedro.util.CUSTOM_INTENT_GEOFENCE
import com.lam.pedro.util.CUSTOM_REQUEST_CODE_GEOFENCE
import kotlinx.coroutines.tasks.await

class GeofenceManager(context: Context) {
    private val TAG = "GeofenceManager"
    private val client = LocationServices.getGeofencingClient(context)
    private val sharedPreferences =
        context.getSharedPreferences("GeofencePreferences", Context.MODE_PRIVATE)
    val geofenceList = mutableMapOf<String, Geofence>()

    private val geofencingPendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            CUSTOM_REQUEST_CODE_GEOFENCE,
            Intent(CUSTOM_INTENT_GEOFENCE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    init {
        loadGeofencesFromPreferences()  // Carica le geofence salvate durante l'inizializzazione
    }

    // Aggiungi una nuova geofence
    fun addGeofence(
        name: String,
        key: String,
        location: Location,
        radiusInMeters: Float = 100.0f,
    ) {
        saveGeofenceName(key, name)  // Salva la coppia key -> name prima di aggiungere la geofence
        geofenceList[key] = createGeofence(key, location, radiusInMeters)
        val geofencingRequest = geofenceList[key]?.let {
            GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(it)
                .build()
        }
        saveGeofencesToPreferences()  // Salva dopo aver aggiunto una geofence
    }

    // Rimuovi una geofence
    fun removeGeofence(key: String) {
        geofenceList.remove(key)
        deregisterGeofenceForKey(key)
        saveGeofencesToPreferences()  // Salva dopo aver rimosso una geofence
    }

    // Registra le geofence
    @SuppressLint("MissingPermission")
    fun registerGeofence() {
        client.addGeofences(createGeofencingRequest(), geofencingPendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "registerGeofence: SUCCESS")
            }.addOnFailureListener { exception ->
                Log.d(TAG, "registerGeofence: Failure\n$exception")
            }
    }

    // Deregistra tutte le geofence
    suspend fun deregisterGeofence() = kotlin.runCatching {
        client.removeGeofences(geofencingPendingIntent).await()
        geofenceList.clear()
        saveGeofencesToPreferences()  // Salva dopo aver deregisitrato tutte le geofence
    }

    fun deregisterGeofenceForKey(key: String) {
        client.removeGeofences(listOf(key))
            .addOnSuccessListener {
                Log.d(TAG, "Geofence for key $key deregistered")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error deregistering geofence for key $key: $exception")
            }
    }

    // Crea una richiesta di geofence
    private fun createGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GEOFENCE_TRANSITION_ENTER)
            addGeofences(geofenceList.values.toList())
        }.build()
    }

    // Crea una geofence da aggiungere alla lista
    private fun createGeofence(
        key: String,
        location: Location,
        radiusInMeters: Float
    ): Geofence {

        return Geofence.Builder()
            .setRequestId(key) // Usa il key calcolato come requestId
            .setCircularRegion(location.latitude, location.longitude, radiusInMeters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(GEOFENCE_TRANSITION_ENTER or GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    // Funzione per salvare la coppia key -> name nelle SharedPreferences
    private fun saveGeofenceName(key: String, name: String) {
        val editor = sharedPreferences.edit()
        // Carica le attuali mappature key -> name se esistono
        val existingGeofences = sharedPreferences.getString("geofenceNames", "") ?: ""

        // Aggiungi la nuova coppia alla mappa
        val newMapping = if (existingGeofences.isNotEmpty()) {
            "$existingGeofences,$key:$name" // Se esistono mappature, aggiungi la nuova coppia
        } else {
            "$key:$name" // Prima coppia da aggiungere
        }

        editor.putString("geofenceNames", newMapping)
        editor.apply() // Salva le modifiche
    }

    // Funzione per caricare la mappa key -> name da SharedPreferences
    private fun loadGeofenceNames(): Map<String, String> {
        val geofenceNamesString = sharedPreferences.getString("geofenceNames", "") ?: ""
        val geofenceNames = mutableMapOf<String, String>()

        if (geofenceNamesString.isNotEmpty()) {
            // Splitto la stringa per ottenere le singole coppie key:name
            val entries = geofenceNamesString.split(",")
            entries.forEach { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val key = parts[0]
                    val name = parts[1]
                    geofenceNames[key] = name
                }
            }
        }

        return geofenceNames
    }


    // Salva tutte le geofence in SharedPreferences
    private fun saveGeofencesToPreferences() {
        val editor = sharedPreferences.edit()
        val geofenceData = geofenceList.entries.joinToString(":") { (key, geofence) ->
            "${key}:${geofence.latitude}:${geofence.longitude}:${geofence.radius}"
        }

        Log.d(
            TAG,
            "Geofences to save: $geofenceData"
        )  // Aggiungi un log per vedere cosa stai cercando di salvare

        editor.putString("geofences", geofenceData)
        val success = editor.commit()
        if (success) {
            Log.d(TAG, "Geofences saved successfully.")
        } else {
            Log.d(TAG, "Failed to save geofences.")
        }
    }


    private fun loadGeofencesFromPreferences() {
        val geofenceData = sharedPreferences.getString("geofences", "") ?: ""

        Log.d(TAG, "Loaded geofences: $geofenceData")  // Log dei dati caricati

        if (geofenceData.isNotEmpty()) {
            val geofences = geofenceData.split(":").chunked(4).mapNotNull { parts ->
                if (parts.size == 4) {
                    val key = parts[0]
                    val latitude = parts[1].toDoubleOrNull()
                    val longitude = parts[2].toDoubleOrNull()
                    val radius = parts[3].toFloatOrNull()

                    if (latitude != null && longitude != null && radius != null) {
                        // Log per verificare i dati di ciascuna geofence
                        Log.d(
                            TAG,
                            "Parsing geofence: Key = $key, Latitude = $latitude, Longitude = $longitude, Radius = $radius"
                        )

                        val location = Location("").apply {
                            this.latitude = latitude
                            this.longitude = longitude
                        }

                        // Crea la geofence
                        key to createGeofence(key, location, radius)
                    } else {
                        Log.e(TAG, "Invalid location or radius data for key: $key")
                        null
                    }
                } else {
                    Log.e(TAG, "Invalid geofence data format.")
                    null
                }
            }

            // Aggiungi le geofence caricate alla lista
            geofences.forEach { (key, geofence) ->
                geofenceList[key] = geofence
            }

            Log.d(TAG, "Geofences loaded successfully: ${geofenceList.keys}")
        } else {
            Log.d(TAG, "No geofences found in SharedPreferences.")
        }
    }

    // Restituisce tutte le geofence salvate
    /*
    fun getSavedLocations(): List<Geofence> {
        return geofenceList.values.toList()
    }

     */
    fun getSavedLocations(): List<Pair<String, Geofence>> {
        // Carica la mappa di key -> name dalle SharedPreferences
        val geofenceNames = loadGeofenceNames()

        // Crea una lista di coppie name -> Geofence
        return geofenceList.entries.mapNotNull { (key, geofence) ->
            val name = geofenceNames[key] // Cerca il nome associato alla geofence
            if (name != null) {
                Pair(name, geofence) // Crea una coppia name -> Geofence
            } else {
                null // Se non c'è un nome associato, ignora questa geofence
            }
        }
    }

}
