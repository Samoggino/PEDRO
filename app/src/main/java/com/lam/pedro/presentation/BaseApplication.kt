
package com.lam.pedro.presentation

import android.app.Application
import com.lam.pedro.BuildConfig
import com.lam.pedro.data.HealthConnectManager
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class BaseApplication : Application() {
    val healthConnectManager by lazy {
        HealthConnectManager(this)
    }

    override fun onCreate() {
        super.onCreate()

        // Inizializza MapLibre
        MapLibre.getInstance(
            this,
            BuildConfig.MAPLIBRE_ACCESS_TOKEN, // Sostituisci con una chiave API valida oppure usa null
            WellKnownTileServer.MapTiler // Usa il tile server appropriato
        )
    }
}

