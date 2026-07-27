package com.dj.tripfuel.tracking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dj.tripfuel.MainActivity
import com.dj.tripfuel.model.NearbyRiderModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.*
import kotlin.random.Random

class NearbyRiderRadarManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var radarJob: Job? = null

    private val _nearbyRiders = MutableStateFlow<List<NearbyRiderModel>>(emptyList())
    val nearbyRiders: StateFlow<List<NearbyRiderModel>> = _nearbyRiders

    private val _isRadarEnabled = MutableStateFlow(true)
    val isRadarEnabled: StateFlow<Boolean> = _isRadarEnabled

    private val NOTIFICATION_CHANNEL_ID = "tripfuel_proximity_channel"
    private val NOTIFICATION_ID = 2002
    private var lastNotifiedRiderId: String? = null
    private var lastNotifiedTime: Long = 0

    init {
        createProximityNotificationChannel()
        startRadarScanning()
    }

    fun toggleRadar(enabled: Boolean) {
        _isRadarEnabled.value = enabled
        if (enabled) {
            startRadarScanning()
        } else {
            radarJob?.cancel()
            _nearbyRiders.value = emptyList()
        }
    }

    private fun startRadarScanning() {
        radarJob?.cancel()
        radarJob = scope.launch {
            // Simulated center position (Mumbai / City Area)
            val centerLat = 19.0760
            val centerLng = 72.8777

            while (isActive && _isRadarEnabled.value) {
                delay(3000L) // Scan & update proximity every 3 seconds

                // Generate active nearby delivery riders in 1km radius
                val mockRiders = listOf(
                    NearbyRiderModel(
                        id = "rider_zomato_1",
                        riderName = "Rahul S.",
                        platform = "Zomato",
                        latitude = centerLat + (Random.nextDouble() - 0.49) * 0.003,
                        longitude = centerLng + (Random.nextDouble() - 0.49) * 0.003,
                        distanceMeters = Random.nextFloat() * 180f + 60f,
                        headingDegrees = Random.nextFloat() * 360f
                    ),
                    NearbyRiderModel(
                        id = "rider_rapido_2",
                        riderName = "Vikram K.",
                        platform = "Rapido",
                        latitude = centerLat + (Random.nextDouble() - 0.49) * 0.004,
                        longitude = centerLng + (Random.nextDouble() - 0.49) * 0.004,
                        distanceMeters = Random.nextFloat() * 350f + 120f,
                        headingDegrees = Random.nextFloat() * 360f
                    ),
                    NearbyRiderModel(
                        id = "rider_swiggy_3",
                        riderName = "Amit P.",
                        platform = "Swiggy",
                        latitude = centerLat + (Random.nextDouble() - 0.49) * 0.005,
                        longitude = centerLng + (Random.nextDouble() - 0.49) * 0.005,
                        distanceMeters = Random.nextFloat() * 600f + 250f,
                        headingDegrees = Random.nextFloat() * 360f
                    )
                )

                _nearbyRiders.value = mockRiders

                // Check for Proximity Crossing Alert (< 100 meters)
                val closestRider = mockRiders.minByOrNull { it.distanceMeters }
                if (closestRider != null && closestRider.distanceMeters <= 100f) {
                    val now = System.currentTimeMillis()
                    if (closestRider.id != lastNotifiedRiderId || (now - lastNotifiedTime) > 30000L) {
                        lastNotifiedRiderId = closestRider.id
                        lastNotifiedTime = now
                        sendProximityNotification(closestRider)
                    }
                }
            }
        }
    }

    private fun createProximityNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "TripFuel Proximity Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies when a fellow delivery rider or friend crosses within 100m range."
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendProximityNotification(rider: NearbyRiderModel) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("🛵 Nearby Rider Alert!")
            .setContentText("${rider.platform} rider (${rider.riderName}) is within ${rider.distanceMeters.toInt()}m range!")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val r = 6371000.0 // Radius of Earth in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toFloat()
    }
}
