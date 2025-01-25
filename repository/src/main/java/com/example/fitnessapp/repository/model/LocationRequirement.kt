package com.example.fitnessapp.repository.model

sealed interface LocationRequirement {

    data object LocationPermissionNeeded : LocationRequirement
    data object GpsActivationNeeded : LocationRequirement
    data object ReadyToCollectLocation : LocationRequirement
}
