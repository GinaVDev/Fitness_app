package com.example.fitnessapp.repositoryimpl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.fitnessapp.repository.LocationStateRepository
import com.example.fitnessapp.repository.model.LocationRequirement
import javax.inject.Inject

class LocationStateRepositoryImpl @Inject constructor(
    private val context: Context
) : LocationStateRepository {

    override suspend fun getLocationRequirement(): LocationRequirement {
        val permission = when {
            !hasLocationPermission() -> LocationRequirement.LocationPermissionNeeded
            !checkGPSEnabled() -> LocationRequirement.GpsActivationNeeded
            else -> LocationRequirement.ReadyToCollectLocation
        }
        Log.d("currentLoc", "repo: $permission")
        return permission
    }

    private fun checkGPSEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isGPSEnabled
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }
}
