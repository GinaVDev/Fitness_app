package com.example.fitnessapp.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.LocationException
import com.example.fitnessapp.extensions.hasLocationPermission
import com.example.fitnessapp.repositoryimpl.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume

class FitnessAppLocationClient @Inject constructor(
    private val context: Context,
    private val locationProviderClient: FusedLocationProviderClient,
) : LocationClient {

    private val _locationFlow = getLocationUpdateFlow()
    override suspend fun getLocationUpdates(): Flow<Location> {
        return _locationFlow.shareIn(
            CoroutineScope(coroutineContext),
            SharingStarted.WhileSubscribed()
        )
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override suspend fun getCurrentLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            locationProviderClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    continuation.resume(task.result)
                } else {
                    continuation.resume(null)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationUpdateFlow(): Flow<Location> {
        return callbackFlow {
            checkPermissions()
            validateLocationProviders()

            val request = LocationRequest.Builder(INTERVAL1SEC)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(10f)
                .setWaitForAccurateLocation(true)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        trySend(location)
                    }
                }
            }

            locationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                locationProviderClient.removeLocationUpdates(locationCallback)
            }
        }.distinctUntilChanged()
    }

    private fun checkPermissions() {
        if (!context.hasLocationPermission()) {
            throw LocationException.PermissionError(message = context.getString(R.string.permission_error_message))
        }
    }

    private fun validateLocationProviders() {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            throw LocationException.GpsError(message = context.getString(R.string.gps_error_message))
        }
    }
}

const val INTERVAL1SEC = 1000L
