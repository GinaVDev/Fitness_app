package com.example.fitnessapp.localdatasourceroom.activity

import com.example.fitnessapp.localdatasource.FitnessActivity
import com.example.fitnessapp.localdatasource.FitnessActivityLocalDataSource
import com.example.fitnessapp.localdatasource.FitnessLocation
import com.example.fitnessapp.localdatasourceroom.mapper.FitnessActivityMapper
import com.example.fitnessapp.localdatasourceroom.mapper.FitnessLocationMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FitnessActivityLocalDataSourceRoom @Inject constructor(
    private val activityDataDao: FitnessActivityDataDao,
    private val fitnessActivityDao: FitnessActivityDao,
    private val fitnessLocationMapper: FitnessLocationMapper,
    private val fitnessActivityMapper: FitnessActivityMapper
) : FitnessActivityLocalDataSource {

    override suspend fun createNewFitnessActivity(): Long {
        val exercise = FitnessActivityEntity()
        val id = fitnessActivityDao.insertActivity(exercise)
        return id
    }

    override suspend fun saveLocationData(location: FitnessLocation) {
        val route = fitnessLocationMapper.mapFromLocation(location)
        activityDataDao.insertActivityData(route)
    }

    override fun getFitnessLocations(activityId: Long): Flow<List<FitnessLocation>> {
        return activityDataDao.getActivityData(activityId).map { locations ->
            fitnessLocationMapper.mapFromEntity(locations)
        }
    }

    override fun getFitnessActivity(): Flow<List<FitnessActivity>> {
        return fitnessActivityDao.getActivity().map { entities ->
            fitnessActivityMapper.mapFromEntity(entities)
        }
    }

    override fun getAllFitnessActivityData(): Flow<List<FitnessLocation>> {
        return activityDataDao.getAllFitnessActivityData().map { entities ->
            fitnessLocationMapper.mapFromEntity(entities)
        }
    }

    override suspend fun getCoordinatesFromLastActivity(): FitnessLocation {
        return withContext(Dispatchers.IO) {
            val lastActivity = activityDataDao.getLastFitnessActivityData()
                ?: throw NoSuchElementException("No fitness activity data found in the database.")
            fitnessLocationMapper.mapFromSingleEntity(lastActivity)
        }
    }
}
