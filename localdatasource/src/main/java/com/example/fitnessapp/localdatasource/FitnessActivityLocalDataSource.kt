package com.example.fitnessapp.localdatasource

import kotlinx.coroutines.flow.Flow

interface FitnessActivityLocalDataSource {
    suspend fun saveLocationData(location: FitnessLocation)
    suspend fun createNewFitnessActivity(): Long
    fun getFitnessLocations(activityId: Long): Flow<List<FitnessLocation>>
    fun getFitnessActivity(): Flow<List<FitnessActivity>>
    fun getAllFitnessActivityData(): Flow<List<FitnessLocation>>
    suspend fun getCoordinatesFromLastActivity(): FitnessLocation
}
