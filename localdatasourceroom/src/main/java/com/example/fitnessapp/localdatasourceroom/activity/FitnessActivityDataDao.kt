package com.example.fitnessapp.localdatasourceroom.activity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessActivityDataDao {

    @Insert
    suspend fun insertActivityData(activityEntity: FitnessActivityDataEntity)

    @Query("SELECT * FROM activity_data_table  WHERE fitness_activity_id = :activityId")
    fun getActivityData(activityId: Long): Flow<List<FitnessActivityDataEntity>>

    @Query("SELECT * FROM activity_data_table ORDER BY fitness_activity_id DESC")
    fun getAllFitnessActivityData(): Flow<List<FitnessActivityDataEntity>>

    @Query(
        """
    SELECT * FROM activity_data_table
    WHERE fitness_activity_id = (
        SELECT fitness_activity_id
        FROM activity_data_table
        ORDER BY fitness_activity_id DESC
        LIMIT 1
    )
    ORDER BY id DESC
    LIMIT 1
    """
    )
    fun getLastFitnessActivityData(): FitnessActivityDataEntity
}
