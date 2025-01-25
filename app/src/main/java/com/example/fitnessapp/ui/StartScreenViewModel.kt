package com.example.fitnessapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.localdatasource.FitnessLocation
import com.example.fitnessapp.repository.LocationRepository
import com.example.fitnessapp.repository.LocationStateRepository
import com.example.fitnessapp.repository.model.CurrentLocation
import com.example.fitnessapp.repository.model.FitnessActivityState
import com.example.fitnessapp.repository.model.LocationRequirement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val locationStateRepository: LocationStateRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _showRationalAlert = MutableStateFlow(false)
    val showRationalAlert = _showRationalAlert.asStateFlow()

    private val _showGpsAlert = MutableStateFlow(false)
    val showGpsAlert = _showGpsAlert.asStateFlow()

    private val _navigateToMapEvent = Channel<Unit>()
    val navigateToMapEvent: Flow<Unit> = _navigateToMapEvent.receiveAsFlow()

    private val _location = MutableStateFlow<CurrentLocation?>(null)
    val location: StateFlow<CurrentLocation?> = _location

    val fitnessActivityState: Flow<FitnessActivityState> = locationRepository.fitnessActivityState

    private val _lastLocation = MutableStateFlow<FitnessLocation?>(null)
    val lastLocation = _lastLocation
        .onStart { getLastCoordinates() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    fun showRationalAlert() {
        _showRationalAlert.update { true }
    }

    fun hideRationalAlert() {
        _showRationalAlert.update { false }
    }

    fun showGpsAlert() {
        _showGpsAlert.update { true }
    }

    fun hideGpsAlert() {
        _showGpsAlert.update { false }
    }

    fun handleLocationPermission() {
        viewModelScope.launch {
            val locationRequirementState = locationStateRepository.getLocationRequirement()
            when (locationRequirementState) {
                is LocationRequirement.LocationPermissionNeeded -> {
                    showRationalAlert()
                }

                is LocationRequirement.GpsActivationNeeded -> {
                    showGpsAlert()
                }

                is LocationRequirement.ReadyToCollectLocation -> {
                    _navigateToMapEvent.send(Unit)
                }
            }
        }
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation()
            _location.update {
                location
            }
            _navigateToMapEvent.send(Unit)
        }
    }

    private fun getLastCoordinates() {
        viewModelScope.launch {
            val lastLocation = locationRepository.getLastCoordinates()
            _lastLocation.update {
                lastLocation
            }
        }
    }
}
