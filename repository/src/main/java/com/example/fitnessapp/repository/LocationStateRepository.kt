package com.example.fitnessapp.repository

import com.example.fitnessapp.repository.model.LocationRequirement

interface LocationStateRepository {
    suspend fun getLocationRequirement(): LocationRequirement
}
