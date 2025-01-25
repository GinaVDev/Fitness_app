package com.example.fitnessapp.uielement

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.fitnessapp.uielement.theme.FitnessAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FScaffold(
    modifier: Modifier = Modifier,
    title: String,
    showNavigationIcon: Boolean = true,
    containerColor: Color = FitnessAppTheme.colorScheme.primary,
    titleContentColor: Color = FitnessAppTheme.colorScheme.onPrimary,
    content: @Composable (PaddingValues) -> Unit,
    onClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = FitnessAppTheme.typography.bodyLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = containerColor,
                    titleContentColor = titleContentColor
                ),
                navigationIcon = {
                    if (showNavigationIcon) {
                        IconButton(
                            onClick = onClick
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null,
                                tint = FitnessAppTheme.colorScheme.onSecondary
                            )
                        }
                    } else {
                        null
                    }
                }
            )
        },
        content = content
    )
}
