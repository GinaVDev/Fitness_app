package com.example.fitnessapp.localdatasourceroom.activity

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FitnessActivityDataEntity::class, FitnessActivityEntity::class], version = 1)
abstract class FitnessActivityDatabase : RoomDatabase() {
    abstract fun fitnessActivityDao(): FitnessActivityDao

    abstract fun fitnessActivityDataDao(): FitnessActivityDataDao
}
