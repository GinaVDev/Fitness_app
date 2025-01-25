package com.example.fitnessapp.repository.model

import com.google.android.gms.maps.model.LatLng

class HistoryDetails(
    val averageSpeedPerKmList: List<Float>,
    val route: List<LatLng>,
    val altitudeList: List<Double>
)
