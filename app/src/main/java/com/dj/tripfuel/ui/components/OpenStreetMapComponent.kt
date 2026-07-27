package com.dj.tripfuel.ui.components

import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dj.tripfuel.model.RoutePoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun OpenStreetMapComponent(
    currentLat: Double,
    currentLng: Double,
    routePoints: List<RoutePoint> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Initialize osmdroid configuration
    remember {
        Configuration.getInstance().userAgentValue = context.packageName
        true
    }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(16.5)
                val startPoint = GeoPoint(currentLat, currentLng)
                controller.setCenter(startPoint)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // Draw route polyline
            if (routePoints.isNotEmpty()) {
                val polyline = Polyline(mapView).apply {
                    val geoPoints = routePoints.map { GeoPoint(it.latitude, it.longitude) }
                    setPoints(geoPoints)
                    outlinePaint.color = AndroidColor.parseColor("#00E676") // Neon Green
                    outlinePaint.strokeWidth = 12f
                    outlinePaint.strokeCap = Paint.Cap.ROUND
                }
                mapView.overlays.add(polyline)
            }

            // Draw current rider location marker
            val currentGeoPoint = GeoPoint(currentLat, currentLng)
            val marker = Marker(mapView).apply {
                position = currentGeoPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Current Rider Location"
            }
            mapView.overlays.add(marker)

            mapView.controller.animateTo(currentGeoPoint)
            mapView.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}
