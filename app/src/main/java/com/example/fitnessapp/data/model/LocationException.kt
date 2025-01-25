package com.example.fitnessapp.data.model

sealed class LocationException(
    override val message: String?,
) : Exception() {
    data class GpsError(
        override val message: String?,
    ) : LocationException(
        message = message,
    )
    data class PermissionError(
        override val message: String?,
    ) : LocationException(
        message = message,
    )
}
