package com.example.fitnessapp.uielement

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.fitnessapp.uielement.theme.FitnessAppTheme

@Composable
fun FTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonDefaults.textShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = FitnessAppTheme.colorScheme.primary
        ),
        elevation = null,
        border = null,
        contentPadding = ButtonDefaults.TextButtonContentPadding,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        FText(
            text = text,
            style = FitnessAppTheme.typography.labelSmall
        )
    }
}
