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

    /**
     * Called when a real nearby rider ping is received over P2P/WebSockets.
     */
    fun registerIncomingRiderPing(rider: NearbyRiderModel) {
        val currentList = _nearbyRiders.value.filterNot { it.id == rider.id }
        _nearbyRiders.value = currentList + rider
    }

    private fun startRadarScanning() {
        radarJob?.cancel()
        radarJob = scope.launch {
            while (isActive && _isRadarEnabled.value) {
                delay(4000L) // Purge expired pings older than 30s

                val now = System.currentTimeMillis()
                val activeRiders = _nearbyRiders.value.filter { (now - it.lastSeenTimestamp) < 30000L }
                _nearbyRiders.value = activeRiders

                // Check for Proximity Crossing Alert (< 100 meters)
                val closestRider = activeRiders.minByOrNull { it.distanceMeters }
                if (closestRider != null && closestRider.distanceMeters <= 100f) {
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
