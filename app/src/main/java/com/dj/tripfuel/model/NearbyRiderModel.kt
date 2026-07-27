package com.dj.tripfuel.model

data class NearbyRiderModel(
    val id: String,
    val riderName: String,
    val platform: String, // "Zomato", "Rapido", "Swiggy", "Uber Moto", "Porter"
    val latitude: Double,
    val longitude: Double,
    val distanceMeters: Float,
    val headingDegrees: Float = 0f,
    val lastSeenTimestamp: Long = System.currentTimeMillis()
)
