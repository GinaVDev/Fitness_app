package com.example.fitnessapp.uielement

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class FIconType {
    data class Drawable(@DrawableRes val icon: Int) : FIconType()
    data class Vector(val icon: ImageVector) : FIconType()
}
