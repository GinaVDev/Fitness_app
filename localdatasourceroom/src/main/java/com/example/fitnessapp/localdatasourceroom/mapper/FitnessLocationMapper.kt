package com.example.fitnessapp.localdatasourceroom.mapper

import com.example.fitnessapp.localdatasource.FitnessLocation
import com.example.fitnessapp.localdatasourceroom.activity.FitnessActivityDataEntity

class FitnessLocationMapper {
    fun mapFromEntity(locations: List<FitnessActivityDataEntity>): List<FitnessLocation> {
        return locations.map { location ->
            FitnessLocation(
                time = location.time,
                activityId = location.activityId,
                speed = location.speed,
                lat = location.lat,
                lng = location.lng,
                altitude = location.altitude
            )
        }
    }

    fun mapFromSingleEntity(activity: FitnessActivityDataEntity): FitnessLocation {
        return FitnessLocation(
            time = activity.time,
            activityId = activity.activityId,
            speed = activity.speed,
            lat = activity.lat,
            lng = activity.lng,
            altitude = activity.altitude
        )
    }

    fun mapFromLocation(location: FitnessLocation): FitnessActivityDataEntity {
        return FitnessActivityDataEntity(
            activityId = location.activityId,
            time = location.time,
            speed = location.speed,
            lat = location.lat,
            lng = location.lng,
            altitude = location.altitude
        )
    }
}
