package com.example.fitnessapp.localdatasourceroom.activity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_data_table",
    foreignKeys = [
        ForeignKey(
            entity = FitnessActivityEntity::class,
            parentColumns = arrayOf("fitness_activity_id"),
            childColumns = arrayOf("fitness_activity_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FitnessActivityDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("fitness_activity_id") val activityId: Long,
    val time: Long,
    val speed: Float,
    val lat: Double,
    val lng: Double,
    val altitude: Double
)
