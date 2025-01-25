package com.example.fitnessapp.di

import android.content.Context
import androidx.room.Room
import com.example.fitnessapp.localdatasource.FitnessActivityLocalDataSource
import com.example.fitnessapp.localdatasourceroom.activity.FitnessActivityDao
import com.example.fitnessapp.localdatasourceroom.activity.FitnessActivityDataDao
import com.example.fitnessapp.localdatasourceroom.activity.FitnessActivityDatabase
import com.example.fitnessapp.localdatasourceroom.activity.FitnessActivityLocalDataSourceRoom
import com.example.fitnessapp.localdatasourceroom.mapper.FitnessActivityMapper
import com.example.fitnessapp.localdatasourceroom.mapper.FitnessLocationMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ActivityDatabaseModule {

    @Provides
    fun provideFitnessActivityLocalDataSource(
        activityDataDao: FitnessActivityDataDao,
        activityDao: FitnessActivityDao,
        fitnessLocationMapper: FitnessLocationMapper,
        fitnessActivityMapper: FitnessActivityMapper
    ): FitnessActivityLocalDataSource {
        return FitnessActivityLocalDataSourceRoom(
            activityDataDao,
            activityDao,
            fitnessLocationMapper,
            fitnessActivityMapper
        )
    }

    @Provides
    @Singleton
    fun provideFitnessActivityDatabase(@ApplicationContext context: Context): FitnessActivityDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            FitnessActivityDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideFitnessActivityDao(database: FitnessActivityDatabase): FitnessActivityDao {
        return database.fitnessActivityDao()
    }

    @Provides
    fun provideFitnessActivityDataDao(database: FitnessActivityDatabase): FitnessActivityDataDao {
        return database.fitnessActivityDataDao()
    }

    @Provides
    fun provideFitnessLocationMapper(): FitnessLocationMapper {
        return FitnessLocationMapper()
    }

    @Provides
    fun provideFitnessActivityMapper(): FitnessActivityMapper {
        return FitnessActivityMapper()
    }
}

const val DATABASE_NAME = "fitness_activity_database"
