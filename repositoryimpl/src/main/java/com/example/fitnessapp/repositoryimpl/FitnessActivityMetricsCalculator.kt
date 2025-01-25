package com.example.fitnessapp.repositoryimpl

import android.location.Location
import com.example.fitnessapp.localdatasource.FitnessLocation
import com.example.fitnessapp.repository.model.DistanceAndDuration
import com.example.fitnessapp.repository.model.RunningStatistic
import javax.inject.Inject
import kotlin.time.Duration

class FitnessActivityMetricsCalculator @Inject constructor(
    private val distanceAndDurationCalculator: DistanceAndDurationCalculator
) {

    fun calculateDistanceAndDuration(locations: List<FitnessLocation>): DistanceAndDuration {
        var distance = 0f
        var duration = Duration.ZERO

        var previousLocation: FitnessLocation? = null

        locations.forEach { newLocation ->
            previousLocation?.let { prevLocation ->
                distance += distanceAndDurationCalculator.getDistance(
                    startPoint = prevLocation,
                    nextPoint = newLocation
                )

                duration += distanceAndDurationCalculator.getDuration(
                    startPointTime = prevLocation.time,
                    nextPointTime = newLocation.time
                )
            }
            previousLocation = newLocation
        }
        return DistanceAndDuration(
            distance = distance,
            duration = duration
        )
    }

    fun calculateTempo(
        locations: Location,
        hourToMinutes: Int = 60,
        conversionNumber: Float = 3.6F
    ): Float {
        return hourToMinutes / (locations.speed * conversionNumber)
    }

    fun calculateAverageSpeed(
        distance: Float,
        duration: Duration,
        conversionNumber: Float = 3600F
    ): Float {
        return (distance / duration.inWholeSeconds) * conversionNumber
    }

    fun calculateAverageSpeedAndAltitudePerKm(
        locations: List<FitnessLocation>,
        conversionNumber: Float = 3600F
    ): RunningStatistic {
        val averageSpeedPerKmList = mutableListOf<Float>(0F)
        var startLocation = locations.first()
        val currentAltitudePerKmList = mutableListOf<Double>(locations.first().altitude)
        var cumulativeDistance = 0f

        var previousLocation: FitnessLocation? = null

        locations.forEach { newLocation ->
            previousLocation?.let { previousLocation ->
                cumulativeDistance += distanceAndDurationCalculator.getDistance(previousLocation, newLocation)

                while (cumulativeDistance >= 1f) {
                    val duration =
                        distanceAndDurationCalculator.getDuration(startLocation.time, newLocation.time).inWholeSeconds
                    val averageSpeedPerKm = (1f / duration) * conversionNumber
                    averageSpeedPerKmList.add(averageSpeedPerKm)

                    currentAltitudePerKmList.add(newLocation.altitude)

                    cumulativeDistance -= 1f
                    startLocation = newLocation
                }
            }
            previousLocation = newLocation
        }
        return RunningStatistic(
            averageSpeedPerKm = averageSpeedPerKmList,
            altitude = currentAltitudePerKmList
        )
    }
}
