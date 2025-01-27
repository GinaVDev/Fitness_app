package com.example.fitnessapp.repositoryimpl

import android.Manifest
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.fitnessapp.localdatasource.FitnessActivity
import com.example.fitnessapp.localdatasource.FitnessActivityLocalDataSource
import com.example.fitnessapp.localdatasource.FitnessLocation
import com.example.fitnessapp.repository.LocationRepository
import com.example.fitnessapp.repository.model.CurrentLocation
import com.example.fitnessapp.repository.model.CurrentLocationMapper
import com.example.fitnessapp.repository.model.DistanceAndDuration
import com.example.fitnessapp.repository.model.FitnessActivityState
import com.example.fitnessapp.repository.model.History
import com.example.fitnessapp.repository.model.HistoryDetails
import com.example.fitnessapp.repository.model.RunningStatistic
import com.example.fitnessapp.repository.model.SpeedInformation
import com.example.fitnessapp.repository.model.Summary
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("TooManyFunctions")
@OptIn(ExperimentalCoroutinesApi::class)
class LocationRepositoryImpl @Inject constructor(
    private val locationClient: LocationClient,
    private val fitnessActivityLocalDataSource: FitnessActivityLocalDataSource,
    private val fitnessActivityMetricsCalculator: FitnessActivityMetricsCalculator,
    private val currentLocationMapper: CurrentLocationMapper
) : LocationRepository {

    private val _activityId = MutableStateFlow<Long?>(null)

    private val _locationListFlow: Flow<List<FitnessLocation>> = _activityId
        .filterNotNull()
        .flatMapLatest { activityId ->
            fitnessActivityLocalDataSource.getFitnessLocations(activityId)
        }

    private val _allFitnessActivity: Flow<List<FitnessActivity>> =
        fitnessActivityLocalDataSource.getFitnessActivity()

    private val _allFitnessLocation: Flow<List<FitnessLocation>> =
        fitnessActivityLocalDataSource.getAllFitnessActivityData()

    override val locationListFlow: Flow<List<FitnessLocation>> = _locationListFlow

    private val _latLngFlow = MutableStateFlow(LatLng(0.0, 0.0))
    override val latLngFlow: Flow<LatLng> = _latLngFlow.asStateFlow()

    private val _isFitnessActivityPaused = MutableStateFlow<Boolean>(false)
    override val isFitnessActivityPaused: StateFlow<Boolean> =
        _isFitnessActivityPaused.asStateFlow()

    override val isFitnessActivityRunning: Flow<Boolean> =
        _activityId.map { activityId ->
            activityId != null
        }

    private var firstLocation: Location? = null

    private var job: Job? = null

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun start() {
        job = scope.launch {
            val activityId = fitnessActivityLocalDataSource.createNewFitnessActivity()
            setActivityId(activityId)
            locationClient.getLocationUpdates().map { newLocation ->

                fitnessActivityLocalDataSource.saveLocationData(
                    FitnessLocation(
                        activityId = activityId,
                        time = newLocation.time,
                        speed = newLocation.speed,
                        lat = newLocation.latitude,
                        lng = newLocation.longitude,
                        altitude = newLocation.altitude
                    )
                )
                _latLngFlow.value = LatLng(newLocation.latitude, newLocation.longitude)
            }.collect()
        }
    }

    override fun stop() {
        job?.cancel()
        firstLocation = null
        _activityId.value = null
        _isFitnessActivityPaused.value = false
    }

    override fun pause() {
        job?.cancel()
        _isFitnessActivityPaused.value = true
    }

    override fun startAfterPause() {
        job = scope.launch {
            combine(
                locationClient.getLocationUpdates(),
                _allFitnessActivity
            ) { newLocation, fitnessActivities ->
                val lastActivityId = fitnessActivities.firstOrNull()?.activityId
                lastActivityId?.let { activityId ->
                    fitnessActivityLocalDataSource.saveLocationData(
                        FitnessLocation(
                            activityId = activityId,
                            time = newLocation.time,
                            speed = newLocation.speed,
                            lat = newLocation.latitude,
                            lng = newLocation.longitude,
                            altitude = newLocation.altitude
                        )
                    )
                    _latLngFlow.value = LatLng(newLocation.latitude, newLocation.longitude)
                    _isFitnessActivityPaused.value = false
                }
            }.collect()
        }
    }

    override suspend fun getLastCoordinates(): FitnessLocation {
        return try {
            fitnessActivityLocalDataSource.getCoordinatesFromLastActivity()
        } catch (e: NoSuchElementException) {
            FitnessLocation(
                activityId = -1L,
                time = 0L,
                speed = 0f,
                lat = 47.4983,
                lng = 19.0408,
                altitude = 0.0
            )
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override suspend fun getCurrentLocation(): CurrentLocation? {
        val location = locationClient.getCurrentLocation()
        return location?.let {
            currentLocationMapper.mapFromLocation(it)
        }
    }

    override val route: Flow<List<LatLng>> =
        _locationListFlow.map { locations ->
            locations.map { location ->
                LatLng(location.lat, location.lng)
            }
        }

    override val distanceAndDuration: Flow<DistanceAndDuration> =
        _locationListFlow.map { newLocations ->
            fitnessActivityMetricsCalculator.calculateDistanceAndDuration(newLocations)
        }.flowOn(Dispatchers.IO)

    override val runningStatistic: Flow<RunningStatistic> =
        _locationListFlow.map { locations ->
            fitnessActivityMetricsCalculator.calculateAverageSpeedAndAltitudePerKm(locations)
        }.flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e("FlowError", "Error calculating average speed", e)
            }

    override val speedInformationFlow: Flow<SpeedInformation> = flow {
        locationClient.getLocationUpdates()
            .combine(distanceAndDuration) { location, distanceAndDuration ->
                SpeedInformation(
                    speed = location.speed.times(M_PER_SEC_TO_KM_PER_HOUR),
                    tempo = fitnessActivityMetricsCalculator.calculateTempo(location),
                    averageSpeed = fitnessActivityMetricsCalculator.calculateAverageSpeed(
                        distanceAndDuration.distance,
                        distanceAndDuration.duration
                    )
                )
            }.collect { speedInformation ->
                emit(speedInformation)
            }
    }.flowOn(Dispatchers.IO)

    override val historyFlow: Flow<List<History>> =
        combine(
            _allFitnessActivity,
            _allFitnessLocation,
            _activityId
        ) { activities, locations, activityId ->
            activities.map { activity ->
                val activityLocations = locations.filter { fitnessLocation ->
                    fitnessLocation.activityId == activity.activityId
                }
                val distanceAndDuration =
                    fitnessActivityMetricsCalculator.calculateDistanceAndDuration(
                        activityLocations
                    )
                History(
                    activityId = activity.activityId,
                    startTime = activityLocations.firstOrNull()?.time ?: 0,
                    distance = distanceAndDuration.distance,
                    duration = distanceAndDuration.duration,
                    averageSpeed = fitnessActivityMetricsCalculator.calculateAverageSpeed(
                        distanceAndDuration.distance,
                        distanceAndDuration.duration
                    ),
                    isRunning = activity.activityId == activityId
                )
            }
        }.flowOn(Dispatchers.IO)

    override val summaryFlow: Flow<Summary> =
        combine(_allFitnessActivity, _allFitnessLocation) { activities, locations ->
            val lastActivityId = activities.firstOrNull()
            val filteredLocations = locations.filter { currentLocation ->
                currentLocation.activityId == lastActivityId?.activityId
            }

            val distanceAndDuration = fitnessActivityMetricsCalculator.calculateDistanceAndDuration(filteredLocations)
            val averageSpeedPerKm =
                fitnessActivityMetricsCalculator.calculateAverageSpeedAndAltitudePerKm(
                    filteredLocations
                )
            Summary(
                totalDistance = distanceAndDuration.distance,
                duration = distanceAndDuration.duration,
                averageSpeed = fitnessActivityMetricsCalculator.calculateAverageSpeed(
                    distanceAndDuration.distance,
                    distanceAndDuration.duration
                ),
                averageSpeedPerKm = averageSpeedPerKm.averageSpeedPerKm
            )
        }.flowOn(Dispatchers.IO)

    override val fitnessActivityState: Flow<FitnessActivityState> =
        combine(_isFitnessActivityPaused, _activityId) { isPaused, activityId ->
            when {
                activityId == null -> FitnessActivityState.NOTRUNNING
                isPaused -> FitnessActivityState.PAUSED
                else -> FitnessActivityState.RUNNING
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getHistoryDetails(activityId: Long): Flow<HistoryDetails> {
        return fitnessActivityLocalDataSource.getFitnessLocations(activityId)
            .map { fitnessLocations ->
                val runningStatistics =
                    fitnessActivityMetricsCalculator.calculateAverageSpeedAndAltitudePerKm(
                        fitnessLocations
                    )

                HistoryDetails(
                    averageSpeedPerKmList = runningStatistics.averageSpeedPerKm,
                    route = fitnessLocations.map { location ->
                        LatLng(location.lat, location.lng)
                    },
                    altitudeList = runningStatistics.altitude
                )
            }
    }

    private fun setActivityId(activityId: Long) {
        _activityId.value = activityId
    }
}

const val M_PER_SEC_TO_KM_PER_HOUR = 3.6F
