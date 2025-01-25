package com.example.fitnessapp.uielement

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun FIcon(
    modifier: Modifier = Modifier,
    iconType: FIconType,
    contentDescription: String? = null,
) {
    when (iconType) {
        is FIconType.Drawable -> {
            Icon(
                painter = painterResource(iconType.icon),
                contentDescription = contentDescription,
                modifier = modifier
                    .size(56.dp),
            )
        }

        is FIconType.Vector -> {
            Icon(
                imageVector = iconType.icon,
                contentDescription = contentDescription,
                modifier = modifier
            )
        }
    }
}
