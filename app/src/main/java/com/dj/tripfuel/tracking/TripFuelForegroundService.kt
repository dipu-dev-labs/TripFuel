package com.dj.tripfuel.tracking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dj.tripfuel.MainActivity
import com.dj.tripfuel.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class TripFuelForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val CHANNEL_ID = "tripfuel_live_channel"
    private val NOTIFICATION_ID = 1001

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification("TripFuel Ride Tracking Active", "Initializing GPS...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Start low-overhead notification updater coroutine
        serviceScope.launch {
            // Service monitors tracking updates efficiently without battery drain
            var lastText = ""
            while (isActive) {
                delay(2000L) // update notification every 2 seconds for battery efficiency
                val state = TripFuelServiceBridge.liveStateFlow?.value
                if (state != null && state.isRiding) {
                    val newText = "Distance: ${String.format("%.2f", state.distanceKm)} km | Fuel: ₹${state.fuelCost.toInt()} | Speed: ${state.currentSpeedKmH.toInt()} km/h"
                    if (newText != lastText) {
                        lastText = newText
                        updateNotification("🛵 Active Ride • ${state.platform}", newText)
                    }
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "TripFuel Live Ride Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows real-time ride distance, speed, and fuel cost in notification bar."
                setShowBadge(false)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, content: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(title, content))
    }

    companion object {
        fun startService(context: Context) {
            val intent = Intent(context, TripFuelForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, TripFuelForegroundService::class.java)
            context.stopService(intent)
        }
    }
}

// Global Bridge object so Foreground Service can observe state with zero memory leaks
object TripFuelServiceBridge {
    var liveStateFlow: kotlinx.coroutines.flow.StateFlow<LiveRideState>? = null
}
