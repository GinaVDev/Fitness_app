package com.example.fitnessapp.repository.model

import android.location.Location

class CurrentLocationMapper {

    fun mapFromLocation(location: Location): CurrentLocation {
        return CurrentLocation(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
}
