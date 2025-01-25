package com.example.fitnessapp.localdatasource

data class FitnessLocation(
    val activityId: Long,
    val time: Long,
    val speed: Float,
    val lat: Double,
    val lng: Double,
    val altitude: Double
)
