/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

