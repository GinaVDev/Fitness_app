package com.example.fitnessapp.repositoryimpl

import android.location.Location
import com.example.fitnessapp.localdatasource.FitnessLocation
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DistanceAndDurationCalculator {

    fun getDistance(
        startPoint: FitnessLocation,
        nextPoint: FitnessLocation,
        conversionNumber: Float = 1000F
    ): Float {
        val distanceBetweenTwoCoordinates = FloatArray(1)

        Location.distanceBetween(
            startPoint.lat,
            startPoint.lng,
            nextPoint.lat,
            nextPoint.lng,
            distanceBetweenTwoCoordinates
        )
        return distanceBetweenTwoCoordinates[0] / conversionNumber
    }

    fun getDuration(
        startPointTime: Long,
        nextPointTime: Long,
        conversionNumber: Int = 1000
    ): Duration {
        val durationMillis = nextPointTime - startPointTime
        val durationSeconds = durationMillis / conversionNumber

        val duration = durationSeconds.toDuration(DurationUnit.SECONDS)
        return duration
    }
}
