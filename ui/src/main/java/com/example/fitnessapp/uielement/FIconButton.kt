package com.example.fitnessapp.uielement

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors,
    interactionSource: MutableInteractionSource? = null,
    iconType: FIconType,
    contentDescription: String? = null
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = {
            FIcon(
                iconType = iconType,
                contentDescription = contentDescription
            )
        }
    )
}
