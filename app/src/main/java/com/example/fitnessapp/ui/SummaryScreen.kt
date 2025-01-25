package com.example.fitnessapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessapp.R
import com.example.fitnessapp.repository.model.Summary
import com.example.fitnessapp.uielement.FScaffold
import com.example.fitnessapp.uielement.FText
import com.example.fitnessapp.uielement.theme.FitnessAppTheme
import kotlin.time.Duration

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel,
    onBackPressed: () -> Unit
) {
    val summaryData by viewModel.summaryFlow.collectAsStateWithLifecycle(
        initialValue = Summary(
            totalDistance = 0f,
            duration = Duration.ZERO,
            averageSpeed = 0f,
            averageSpeedPerKm = emptyList()
        )
    )

    BackHandler {
        onBackPressed()
    }

    FScaffold(
        title = stringResource(R.string.summary_screen_name),
        showNavigationIcon = true,
        onClick = onBackPressed,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues),
                ) {
                    FText(
                        text = stringResource(
                            R.string.total_distance_text_label_unit_of_measure,
                            summaryData.totalDistance
                        ),
                        style = FitnessAppTheme.typography.displayMedium
                    )
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )
                    FText(
                        text =
                        summaryData.duration.toComponents { hours, minutes, seconds, _ ->
                            stringResource(
                                R.string.duration_text_label_units_of_measure,
                                hours,
                                minutes,
                                seconds
                            )
                        },
                        style = FitnessAppTheme.typography.displayMedium
                    )
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )
                    FText(
                        text = stringResource(
                            R.string.average_speed_text_label_unit_of_measure,
                            summaryData.averageSpeed
                        ),
                        style = FitnessAppTheme.typography.displayMedium
                    )
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )
                    FText(
                        text = stringResource(R.string.average_speed_per_kilomoters_text_label),
                        style = FitnessAppTheme.typography.displayMedium
                    )
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                    )
                    LazyColumn {
                        itemsIndexed(summaryData.averageSpeedPerKm.drop(1)) { index, speed ->
                            Text(
                                text = "${index + 1}" + " " +
                                    stringResource(R.string.distance_unit_of_measure) +
                                    " " + stringResource(
                                        R.string.speed_unit_of_measure, speed
                                    ),
                                style = FitnessAppTheme.typography.displayMedium
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}
