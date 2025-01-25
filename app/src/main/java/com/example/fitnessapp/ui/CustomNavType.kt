package com.example.fitnessapp.ui

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.fitnessapp.repository.model.CurrentLocation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomNavType {

    val location = object : NavType<CurrentLocation?>(
        isNullableAllowed = true
    ) {

        override fun get(bundle: Bundle, key: String): CurrentLocation? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): CurrentLocation? {
            return Json.decodeFromString(value)
        }

        override fun serializeAsValue(value: CurrentLocation?): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: CurrentLocation?) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}
