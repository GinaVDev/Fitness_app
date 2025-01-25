package com.example.fitnessapp.ui

import androidx.lifecycle.ViewModel
import com.example.fitnessapp.repository.LocationRepository
import com.example.fitnessapp.repository.model.DistanceAndDuration
import com.example.fitnessapp.repository.model.FitnessActivityState
import com.example.fitnessapp.repository.model.SpeedInformation
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    val route: Flow<List<LatLng>> = locationRepository.route
    val distanceAndDuration: Flow<DistanceAndDuration> = locationRepository.distanceAndDuration
    val speedInformation: Flow<SpeedInformation> = locationRepository.speedInformationFlow
    val fitnessActivityState: Flow<FitnessActivityState> = locationRepository.fitnessActivityState
}
