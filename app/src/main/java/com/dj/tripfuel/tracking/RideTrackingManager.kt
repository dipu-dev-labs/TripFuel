package com.dj.tripfuel.tracking

import com.dj.tripfuel.model.RideSession
import com.dj.tripfuel.model.RoutePoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

data class LiveRideState(
    val isRiding: Boolean = false,
    val distanceKm: Float = 0.0f,
    val durationSec: Long = 0,
    val currentSpeedKmH: Float = 0.0f,
    val fuelUsedL: Float = 0.0f,
    val fuelCost: Float = 0.0f,
    val earnings: Float = 0.0f,
    val platform: String = "Rapido",
    val netProfit: Float = 0.0f,
    val gpsAccuracyMeters: Float = 3.5f,
    val isBatterySaver: Boolean = false,
    val isSimulationMode: Boolean = false, // false = Real GPS Mode (Stationary when not moving)
    val routePoints: List<RoutePoint> = emptyList()
)

class RideTrackingManager {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var trackingJob: Job? = null

    private val _liveState = MutableStateFlow(LiveRideState())
    val liveState: StateFlow<LiveRideState> = _liveState

    // Configuration params
    var mileageKmL: Float = 55.0f
    var petrolPricePerL: Float = 104.50f

    // Initial origin coordinates (Mumbai city area)
    private var lastLat = 19.0760
    private var lastLng = 72.8777

    fun startRide(mileage: Float, petrolPrice: Float) {
        if (_liveState.value.isRiding) return
        this.mileageKmL = if (mileage > 0) mileage else 55.0f
        this.petrolPricePerL = if (petrolPrice > 0) petrolPrice else 104.50f

        val initialPoints = listOf(RoutePoint(lastLat, lastLng))
        _liveState.value = LiveRideState(
            isRiding = true,
            distanceKm = 0.0f,
            durationSec = 0,
            currentSpeedKmH = 0.0f,
            fuelUsedL = 0.0f,
            fuelCost = 0.0f,
            earnings = 0.0f,
            netProfit = 0.0f,
            isSimulationMode = false,
            routePoints = initialPoints
        )

        trackingJob?.cancel()
        trackingJob = scope.launch {
            while (isActive && _liveState.value.isRiding) {
                delay(1000L) // tick every 1 second
                val current = _liveState.value
                val newDuration = current.durationSec + 1

                if (current.isSimulationMode) {
                    // DEMO SIMULATION MODE: Simulates delivery rider speed & distance
                    val simulatedSpeed = kotlin.random.Random.nextFloat() * 18.0f + 25.0f // 25 to 43 km/h
                    val distanceAddedKm = (simulatedSpeed / 3600.0f)
                    val newDistance = current.distanceKm + distanceAddedKm

                    val newFuelUsed = if (mileageKmL > 0) newDistance / mileageKmL else 0.0f
                    val newFuelCost = newFuelUsed * petrolPricePerL
                    val newNetProfit = current.earnings - newFuelCost

                    lastLat += (kotlin.random.Random.nextDouble() - 0.48) * 0.0003
                    lastLng += (kotlin.random.Random.nextDouble() - 0.48) * 0.0003
                    val updatedPoints = current.routePoints + RoutePoint(lastLat, lastLng)

                    _liveState.value = current.copy(
                        durationSec = newDuration,
                        distanceKm = newDistance,
                        currentSpeedKmH = simulatedSpeed,
                        fuelUsedL = newFuelUsed,
                        fuelCost = newFuelCost,
                        netProfit = newNetProfit,
                        routePoints = updatedPoints.takeLast(100)
                    )
                } else {
                    // REAL GPS MODE: When phone is stationary, distance & speed remain 0.0
                    // Only actual location updates / movement change distance & speed!
                    val newFuelUsed = if (mileageKmL > 0) current.distanceKm / mileageKmL else 0.0f
                    val newFuelCost = newFuelUsed * petrolPricePerL
                    val newNetProfit = current.earnings - newFuelCost

                    _liveState.value = current.copy(
                        durationSec = newDuration,
                        currentSpeedKmH = 0.0f, // 0 km/h when stationary
                        fuelUsedL = newFuelUsed,
                        fuelCost = newFuelCost,
                        netProfit = newNetProfit
                    )
                }
            }
        }
    }

    // Called when actual GPS location sensor reports location update or simulated location change
    fun onLocationUpdate(latitude: Double, longitude: Double, speedKmH: Float) {
        val current = _liveState.value
        if (!current.isRiding || current.isSimulationMode) return

        // Compute distance from last coordinate
        val prevPoint = current.routePoints.lastOrNull()
        val distanceAddedKm = if (prevPoint != null) {
            calculateDistanceKm(prevPoint.latitude, prevPoint.longitude, latitude, longitude)
        } else 0f

        val newDistance = current.distanceKm + distanceAddedKm
        val newFuelUsed = if (mileageKmL > 0) newDistance / mileageKmL else 0.0f
        val newFuelCost = newFuelUsed * petrolPricePerL
        val newNetProfit = current.earnings - newFuelCost

        val updatedPoints = current.routePoints + RoutePoint(latitude, longitude)

        _liveState.value = current.copy(
            distanceKm = newDistance,
            currentSpeedKmH = speedKmH,
            fuelUsedL = newFuelUsed,
            fuelCost = newFuelCost,
            netProfit = newNetProfit,
            routePoints = updatedPoints.takeLast(100)
        )
    }

    fun toggleSimulationMode() {
        val current = _liveState.value
        _liveState.value = current.copy(isSimulationMode = !current.isSimulationMode)
    }

    fun addEarnings(amount: Float, platformName: String) {
        val current = _liveState.value
        val newEarnings = current.earnings + amount
        val newNetProfit = newEarnings - current.fuelCost
        _liveState.value = current.copy(
            earnings = newEarnings,
            platform = platformName,
            netProfit = newNetProfit
        )
    }

    fun toggleBatterySaver() {
        _liveState.value = _liveState.value.copy(
            isBatterySaver = !_liveState.value.isBatterySaver
        )
    }

    fun stopRide(): RideSession {
        trackingJob?.cancel()
        val finalState = _liveState.value
        val now = System.currentTimeMillis()
        val df = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

        val completedRide = RideSession(
            id = "ride_${UUID.randomUUID().toString().take(8)}",
            dateTimestamp = now,
            dateFormatted = df.format(Date(now)),
            distanceKm = finalState.distanceKm,
            durationSec = finalState.durationSec,
            avgSpeedKmH = if (finalState.durationSec > 0) (finalState.distanceKm / (finalState.durationSec / 3600.0f)) else 0f,
            fuelUsedL = finalState.fuelUsedL,
            fuelCost = finalState.fuelCost,
            earnings = finalState.earnings,
            platform = finalState.platform,
            netProfit = finalState.netProfit
        )

        _liveState.value = LiveRideState(isRiding = false)
        return completedRide
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val r = 6371.0 // Radius of Earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (r * c).toFloat()
    }
}
