package com.dj.tripfuel.model

data class UserSettings(
    val riderName: String = "Dipu",
    val bikeName: String = "Honda Shine",
    val bikeMileage: Float = 55.0f,
    val petrolPrice: Float = 104.5f,
    val currencySymbol: String = "₹",
    val distanceUnit: String = "km",
    val isDarkMode: Boolean = true,
    val isSetupCompleted: Boolean = false,
    val activeBikeId: String = "default_bike_1"
)

data class Bike(
    val id: String,
    val nickname: String,
    val mileage: Float,
    val fuelType: String = "Petrol",
    val isDefault: Boolean = false
)

data class RoutePoint(
    val latitude: Double,
    val longitude: Double
)

data class RideSession(
    val id: String,
    val dateTimestamp: Long,
    val dateFormatted: String,
    val distanceKm: Float,
    val durationSec: Long,
    val avgSpeedKmH: Float,
    val fuelUsedL: Float,
    val fuelCost: Float,
    val earnings: Float,
    val platform: String = "Custom",
    val netProfit: Float,
    val routePointsJson: String = ""
)

data class FuelLog(
    val id: String,
    val dateFormatted: String,
    val amountPaid: Float,
    val litres: Float,
    val petrolPrice: Float,
    val stationName: String,
    val notes: String = ""
)

data class MaintenanceItem(
    val id: String,
    val title: String,
    val lastServiceKm: Float,
    val intervalKm: Float,
    val currentKm: Float,
    val dueDateFormatted: String,
    val notes: String = ""
) {
    val isDue: Boolean
        get() = (currentKm - lastServiceKm) >= intervalKm
}
