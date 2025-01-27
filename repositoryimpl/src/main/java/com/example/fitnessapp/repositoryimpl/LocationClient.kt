package com.example.fitnessapp.repositoryimpl

import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    suspend fun getLocationUpdates(): Flow<Location>

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    suspend fun getCurrentLocation(): Location?
}
