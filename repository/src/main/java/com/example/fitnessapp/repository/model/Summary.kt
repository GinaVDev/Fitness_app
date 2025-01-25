package com.example.fitnessapp.repository.model

import kotlin.time.Duration

data class Summary(
    val totalDistance: Float,
    val duration: Duration,
    val averageSpeed: Float,
    val averageSpeedPerKm: List<Float>
)
