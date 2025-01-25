package com.example.fitnessapp.localdatasourceroom.activity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessActivityDao {

    @Insert
    suspend fun insertActivity(activityEntity: FitnessActivityEntity): Long

    @Query("SELECT * FROM activity_table ORDER BY fitness_activity_id DESC")
    fun getActivity(): Flow<List<FitnessActivityEntity>>
}
