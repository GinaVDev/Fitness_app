package com.example.fitnessapp.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.example.fitnessapp.localdatasource.FitnessLocation
import com.example.fitnessapp.repository.model.CurrentLocation
import com.example.fitnessapp.repository.model.DistanceAndDuration
import com.example.fitnessapp.repository.model.FitnessActivityState
import com.example.fitnessapp.repository.model.History
import com.example.fitnessapp.repository.model.HistoryDetails
import com.example.fitnessapp.repository.model.RunningStatistic
import com.example.fitnessapp.repository.model.SpeedInformation
import com.example.fitnessapp.repository.model.Summary
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {

    val locationListFlow: Flow<List<FitnessLocation>>
    val latLngFlow: Flow<LatLng>
    val speedInformationFlow: Flow<SpeedInformation>
    val distanceAndDuration: Flow<DistanceAndDuration>
    val runningStatistic: Flow<RunningStatistic>
    val historyFlow: Flow<List<History>>
    val isFitnessActivityRunning: Flow<Boolean>
    val isFitnessActivityPaused: StateFlow<Boolean>
    val route: Flow<List<LatLng>>
    val summaryFlow: Flow<Summary>
    val fitnessActivityState: Flow<FitnessActivityState>

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun start()
    fun stop()
    fun pause()
    fun startAfterPause()
    suspend fun getLastCoordinates(): FitnessLocation

    suspend fun getCurrentLocation(): CurrentLocation?
    suspend fun getHistoryDetails(activityId: Long): Flow<HistoryDetails>
}
