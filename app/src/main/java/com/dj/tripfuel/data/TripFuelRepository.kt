package com.dj.tripfuel.data

import android.content.Context
import android.content.SharedPreferences
import com.dj.tripfuel.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class TripFuelRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tripfuel_local_db", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Key Constants
    private val KEY_SETTINGS = "user_settings"
    private val KEY_BIKES = "bikes_list"
    private val KEY_RIDES = "rides_list"
    private val KEY_FUEL_LOGS = "fuel_logs_list"
    private val KEY_MAINTENANCE = "maintenance_list"

    init {
        // Initialize default seed data if empty
        if (!prefs.contains(KEY_SETTINGS)) {
            val defaultSettings = UserSettings(
                riderName = "Dipu",
                bikeName = "Honda Shine 125",
                bikeMileage = 55.0f,
                petrolPrice = 104.50f,
                currencySymbol = "₹",
                distanceUnit = "km",
                isDarkMode = true,
                isSetupCompleted = false,
                activeBikeId = "bike_1"
            )
            saveSettings(defaultSettings)
        }

        if (!prefs.contains(KEY_BIKES)) {
            val defaultBikes = listOf(
                Bike(id = "bike_1", nickname = "Honda Shine 125", mileage = 55.0f, fuelType = "Petrol", isDefault = true),
                Bike(id = "bike_2", nickname = "TVS King EV", mileage = 85.0f, fuelType = "Electric", isDefault = false)
            )
            saveBikes(defaultBikes)
        }

        if (!prefs.contains(KEY_RIDES)) {
            val df = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            val now = System.currentTimeMillis()
            val seedRides = listOf(
                RideSession(
                    id = "ride_101",
                    dateTimestamp = now - 3600000 * 4,
                    dateFormatted = df.format(Date(now - 3600000 * 4)),
                    distanceKm = 42.5f,
                    durationSec = 5400,
                    avgSpeedKmH = 28.3f,
                    fuelUsedL = 0.77f,
                    fuelCost = 80.50f,
                    earnings = 450.0f,
                    platform = "Zomato",
                    netProfit = 369.50f
                ),
                RideSession(
                    id = "ride_102",
                    dateTimestamp = now - 3600000 * 24,
                    dateFormatted = df.format(Date(now - 3600000 * 24)),
                    distanceKm = 68.2f,
                    durationSec = 8100,
                    avgSpeedKmH = 30.1f,
                    fuelUsedL = 1.24f,
                    fuelCost = 129.50f,
                    earnings = 780.0f,
                    platform = "Rapido",
                    netProfit = 650.50f
                ),
                RideSession(
                    id = "ride_103",
                    dateTimestamp = now - 3600000 * 48,
                    dateFormatted = df.format(Date(now - 3600000 * 48)),
                    distanceKm = 51.0f,
                    durationSec = 6300,
                    avgSpeedKmH = 29.0f,
                    fuelUsedL = 0.93f,
                    fuelCost = 97.20f,
                    earnings = 590.0f,
                    platform = "Swiggy",
                    netProfit = 492.80f
                )
            )
            saveRides(seedRides)
        }

        if (!prefs.contains(KEY_FUEL_LOGS)) {
            val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val now = System.currentTimeMillis()
            val seedFuel = listOf(
                FuelLog(
                    id = "fuel_1",
                    dateFormatted = df.format(Date(now - 86400000L * 2)),
                    amountPaid = 500.0f,
                    litres = 4.78f,
                    petrolPrice = 104.50f,
                    stationName = "Indian Oil Petrol Pump",
                    notes = "Full tank refill"
                ),
                FuelLog(
                    id = "fuel_2",
                    dateFormatted = df.format(Date(now - 86400000L * 7)),
                    amountPaid = 400.0f,
                    litres = 3.82f,
                    petrolPrice = 104.50f,
                    stationName = "HP Fuel Station",
                    notes = "Regular refill"
                )
            )
            saveFuelLogs(seedFuel)
        }

        if (!prefs.contains(KEY_MAINTENANCE)) {
            val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val seedMaintenance = listOf(
                MaintenanceItem(
                    id = "maint_1",
                    title = "Engine Oil Change",
                    lastServiceKm = 12000f,
                    intervalKm = 3000f,
                    currentKm = 14250f,
                    dueDateFormatted = df.format(Date(System.currentTimeMillis() + 86400000L * 15)),
                    notes = "Use 10W-30 Synthetic Oil"
                ),
                MaintenanceItem(
                    id = "maint_2",
                    title = "Chain Cleaning & Lube",
                    lastServiceKm = 13800f,
                    intervalKm = 500f,
                    currentKm = 14250f,
                    dueDateFormatted = "Immediate",
                    notes = "Lube chain every 500 km"
                ),
                MaintenanceItem(
                    id = "maint_3",
                    title = "Brake Pads Inspection",
                    lastServiceKm = 10000f,
                    intervalKm = 5000f,
                    currentKm = 14250f,
                    dueDateFormatted = df.format(Date(System.currentTimeMillis() + 86400000L * 45)),
                    notes = "Front disc & rear drum"
                )
            )
            saveMaintenance(seedMaintenance)
        }
    }

    // --- USER SETTINGS ---
    fun getSettings(): UserSettings {
        val json = prefs.getString(KEY_SETTINGS, null) ?: return UserSettings()
        return try {
            gson.fromJson(json, UserSettings::class.java)
        } catch (e: Exception) {
            UserSettings()
        }
    }

    fun saveSettings(settings: UserSettings) {
        prefs.edit().putString(KEY_SETTINGS, gson.toJson(settings)).apply()
    }

    // --- BIKES ---
    fun getBikes(): List<Bike> {
        val json = prefs.getString(KEY_BIKES, null) ?: return emptyList()
        val type = object : TypeToken<List<Bike>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveBikes(bikes: List<Bike>) {
        prefs.edit().putString(KEY_BIKES, gson.toJson(bikes)).apply()
    }

    fun addBike(bike: Bike) {
        val list = getBikes().toMutableList()
        list.add(bike)
        saveBikes(list)
    }

    // --- RIDE SESSIONS ---
    fun getRides(): List<RideSession> {
        val json = prefs.getString(KEY_RIDES, null) ?: return emptyList()
        val type = object : TypeToken<List<RideSession>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveRides(rides: List<RideSession>) {
        prefs.edit().putString(KEY_RIDES, gson.toJson(rides)).apply()
    }

    fun addRide(ride: RideSession) {
        val list = getRides().toMutableList()
        list.add(0, ride) // insert latest at front
        saveRides(list)
    }

    fun deleteRide(rideId: String) {
        val list = getRides().filterNot { it.id == rideId }
        saveRides(list)
    }

    fun clearAllRides() {
        saveRides(emptyList())
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
    }

    // --- FUEL LOGS ---
    fun getFuelLogs(): List<FuelLog> {
        val json = prefs.getString(KEY_FUEL_LOGS, null) ?: return emptyList()
        val type = object : TypeToken<List<FuelLog>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveFuelLogs(logs: List<FuelLog>) {
        prefs.edit().putString(KEY_FUEL_LOGS, gson.toJson(logs)).apply()
    }

    fun addFuelLog(log: FuelLog) {
        val list = getFuelLogs().toMutableList()
        list.add(0, log)
        saveFuelLogs(list)
    }

    // --- MAINTENANCE ---
    fun getMaintenance(): List<MaintenanceItem> {
        val json = prefs.getString(KEY_MAINTENANCE, null) ?: return emptyList()
        val type = object : TypeToken<List<MaintenanceItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveMaintenance(items: List<MaintenanceItem>) {
        prefs.edit().putString(KEY_MAINTENANCE, gson.toJson(items)).apply()
    }

    fun addMaintenanceItem(item: MaintenanceItem) {
        val list = getMaintenance().toMutableList()
        list.add(item)
        saveMaintenance(list)
    }
}
