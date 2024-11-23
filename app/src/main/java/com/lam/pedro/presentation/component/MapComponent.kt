package com.lam.pedro.presentation.component

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun MapComponent(modifier: Modifier = Modifier, positions: List<LatLng>) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                getMapAsync { maplibreMap ->
                    maplibreMap.setStyle(Style.getPredefinedStyle("Streets")) {
                        // Aggiungi il percorso sulla mappa
                        addRoute(maplibreMap, positions)
                    }
                }
            }
        },
        update = { /* Puoi aggiungere logica di aggiornamento qui */ }
    )
}

fun addRoute(map: MapLibreMap, positions: List<LatLng>) {
    // Converti la lista di posizioni in un LineString
    val lineString = LineString.fromLngLats(positions.map { Point.fromLngLat(it.longitude, it.latitude) })

    // Crea una sorgente GeoJson per il percorso
    val routeSource = GeoJsonSource("route-source", lineString)

    map.getStyle { style ->
        style.addSource(routeSource)


        // Crea un livello LineLayer per mostrare il percorso
        val routeLayer = LineLayer("route-layer", "route-source").withProperties(
            PropertyFactory.lineColor("#0000FF"),
            PropertyFactory.lineWidth(5f)
        )
        style.addLayer(routeLayer)
    }
}
