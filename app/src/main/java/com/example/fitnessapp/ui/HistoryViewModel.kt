package com.example.fitnessapp.ui

import androidx.lifecycle.ViewModel
import com.example.fitnessapp.repository.LocationRepository
import com.example.fitnessapp.repository.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    locationRepository: LocationRepository
) : ViewModel() {

    val historyFlow: Flow<List<History>> = locationRepository.historyFlow
}
