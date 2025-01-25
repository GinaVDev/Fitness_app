package com.example.fitnessapp.localdatasourceroom.mapper

import com.example.fitnessapp.localdatasource.FitnessActivity
import com.example.fitnessapp.localdatasourceroom.activity.FitnessActivityEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FitnessActivityMapper {

    suspend fun mapFromEntity(entities: List<FitnessActivityEntity>): List<FitnessActivity> {
        return withContext(Dispatchers.IO) {
            entities.map { entitiy ->
                FitnessActivity(
                    activityId = entitiy.id
                )
            }
        }
    }
}
