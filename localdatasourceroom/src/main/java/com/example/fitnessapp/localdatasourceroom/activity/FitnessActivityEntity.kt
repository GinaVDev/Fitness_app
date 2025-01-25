package com.example.fitnessapp.localdatasourceroom.activity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_table")
class FitnessActivityEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("fitness_activity_id")
    val id: Long = 0
)
