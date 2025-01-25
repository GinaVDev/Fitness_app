package com.example.fitnessapp.di

import android.content.Context
import com.example.fitnessapp.data.FitnessAppLocationClient
import com.example.fitnessapp.data.LocationService
import com.example.fitnessapp.repository.LocationRepository
import com.example.fitnessapp.repository.LocationStateRepository
import com.example.fitnessapp.repository.model.CurrentLocationMapper
import com.example.fitnessapp.repositoryimpl.DistanceAndDurationCalculator
import com.example.fitnessapp.repositoryimpl.FitnessActivityMetricsCalculator
import com.example.fitnessapp.repositoryimpl.LocationClient
import com.example.fitnessapp.repositoryimpl.LocationRepositoryImpl
import com.example.fitnessapp.repositoryimpl.LocationStateRepositoryImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationDependencies {

    @Singleton
    @Binds
    abstract fun bindLocationClient(
        impl: FitnessAppLocationClient
    ): LocationClient

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindLocationStateRepository(impl: LocationStateRepositoryImpl): LocationStateRepository

    companion object {

        @Provides
        fun provideAppContext(@ApplicationContext context: Context): Context {
            return context
        }

        @Provides
        fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }

        @Provides
        fun provideLocationService(): LocationService {
            return LocationService()
        }

        @Provides
        fun provideFitnessActivityMetricsCalculator(
            distanceAndDurationCalculator: DistanceAndDurationCalculator
        ): FitnessActivityMetricsCalculator {
            return FitnessActivityMetricsCalculator(distanceAndDurationCalculator)
        }

        @Provides
        fun provideDistanceAndDurationCalculator(): DistanceAndDurationCalculator {
            return DistanceAndDurationCalculator()
        }

        @Provides
        fun provideCurrentLocationMapper(): CurrentLocationMapper {
            return CurrentLocationMapper()
        }
    }
}
