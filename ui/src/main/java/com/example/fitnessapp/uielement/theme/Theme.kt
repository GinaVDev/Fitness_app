package com.example.fitnessapp.uielement.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun FitnessAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content:
    @Composable()
    () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = primaryLight.toArgb()
            window.navigationBarColor = primaryLight.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(
        value = LocalColorScheme provides colorScheme
    ) {
    }

    MaterialTheme(
        colorScheme = colorScheme.getM3Colors(),
        typography = Typography,
        content = content
    )
}

object FitnessAppTheme {

    val colorScheme: FColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current

    val typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

internal val LocalTypography = staticCompositionLocalOf { Typography }
internal val LocalColorScheme = staticCompositionLocalOf { lightColorScheme }
