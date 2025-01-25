package com.example.fitnessapp.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrentLocation(
    val latitude: Double,
    val longitude: Double
)
