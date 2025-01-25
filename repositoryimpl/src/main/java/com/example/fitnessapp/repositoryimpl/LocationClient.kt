package com.example.fitnessapp.repositoryimpl

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    suspend fun getLocationUpdates(): Flow<Location>
    suspend fun getCurrentLocation(): Location?
}
