/*
package com.lam.pedro.presentation.component

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.geometry.LatLng

@Composable
fun MapComponent(modifier: Modifier = Modifier, positions: List<LatLng>, color: Color) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                getMapAsync { maplibreMap ->
                    maplibreMap.setStyle(Style.getPredefinedStyle("Streets")) {
                        // Aggiungi il percorso sulla mappa
                        addRoute(maplibreMap, positions, color)
                    }
                }
            }
        },
        update = { /* Puoi aggiungere logica di aggiornamento qui */ }
    )
}

fun addRoute(map: MapLibreMap, positions: List<LatLng>, color: Color) {
    if (positions.isEmpty()) {
        Log.e("MapComponent", "La lista delle posizioni è vuota")
        return
    }

    // Converti la lista di posizioni in un LineString
    val lineString = LineString.fromLngLats(positions.map { Point.fromLngLat(it.longitude, it.latitude) })

    // Crea una sorgente GeoJson per il percorso
    val routeSource = GeoJsonSource("route-source", lineString)

    val colorHex = String.format("#%06X", 0xFFFFFF and color.toArgb())

    map.getStyle { style ->
        style.addSource(routeSource)

        // Crea un livello LineLayer per mostrare il percorso
        val routeLayer = LineLayer("route-layer", "route-source").withProperties(
            PropertyFactory.lineColor(colorHex),
            PropertyFactory.lineWidth(50f)
        )
        style.addLayer(routeLayer)

        Log.d("MapComponent", "---------------------------------------Percorso aggiunto alla mappa--------------------------------------")
    }
}

 */

package com.lam.pedro.presentation.component

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds

@Composable
fun MapComponent(modifier: Modifier = Modifier, positions: List<LatLng>, color: Color) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                getMapAsync { mapLibreMap ->
                    mapLibreMap.setStyle(Style.getPredefinedStyle("Streets")) { style ->
                        // Aggiungi il percorso sulla mappa
                        addRoute(mapLibreMap, style, positions, color)

                        // Centra la mappa sul percorso
                        centerMapOnRoute(mapLibreMap, positions)
                    }
                }
            }
        }
    )
}

fun addRoute(map: MapLibreMap, style: Style, positions: List<LatLng>, color: Color) {
    if (positions.isEmpty()) {
        Log.e("MapComponent", "La lista delle posizioni è vuota")
        return
    }

    // Converti la lista di posizioni in un LineString
    val lineString = LineString.fromLngLats(positions.map { Point.fromLngLat(it.longitude, it.latitude) })

    // Crea una sorgente GeoJson per il percorso
    val routeSource = GeoJsonSource("route-source", lineString)
    style.addSource(routeSource)

    val colorHex = String.format("#%06X", 0xFFFFFF and color.toArgb())

    // Crea un livello LineLayer per mostrare il percorso
    val routeLayer = LineLayer("route-layer", "route-source").withProperties(
        PropertyFactory.lineColor(colorHex),
        PropertyFactory.lineWidth(5f)
    )
    style.addLayer(routeLayer)

    Log.d("MapComponent", "Percorso aggiunto alla mappa")
}

fun centerMapOnRoute(map: MapLibreMap, positions: List<LatLng>) {
    if (positions.isEmpty()) {
        Log.e("MapComponent", "Impossibile centrare la mappa: lista posizioni vuota")
        return
    }

    val boundsBuilder = LatLngBounds.Builder()
    positions.forEach { position ->
        boundsBuilder.include(position)
    }

    val bounds = boundsBuilder.build()
    val padding = 100 // Padding in pixel tra il percorso e i bordi della mappa

    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    Log.d("MapComponent", "Mappa centrata sul percorso")
}
