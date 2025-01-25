package com.example.fitnessapp.repository.model

import kotlin.time.Duration

data class History(
    val activityId: Long,
    val startTime: Long,
    val distance: Float,
    val duration: Duration,
    val averageSpeed: Float,
    val isRunning: Boolean
)
