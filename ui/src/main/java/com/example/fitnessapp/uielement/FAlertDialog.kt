package com.example.fitnessapp.uielement

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fitnessapp.uielement.theme.FitnessAppTheme

@Composable
fun FAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    confirmButtonText: String,
    cancelButtonText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        // .background(FitnessAppTheme.colorScheme.surfaceContainerHigh),
        onDismissRequest = onCancel,
        title = {
            Text(
                text = title,
                style = FitnessAppTheme.typography.labelSmall
            )
        },
        text = {
            Text(
                text = message,
                style = FitnessAppTheme.typography.displayMedium
            )
        },
        confirmButton = {
            FTextButton(
                onClick = onConfirm,
                text = confirmButtonText
            )
        },
        dismissButton = {
            FTextButton(
                onClick = onCancel,
                text = cancelButtonText
            )
        }
    )
}
