package com.example.fitnessapp.ui

import androidx.lifecycle.ViewModel
import com.example.fitnessapp.repository.LocationRepository
import com.example.fitnessapp.repository.model.Summary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    locationRepository: LocationRepository
) : ViewModel() {

    val summaryFlow: Flow<Summary> = locationRepository.summaryFlow
}
