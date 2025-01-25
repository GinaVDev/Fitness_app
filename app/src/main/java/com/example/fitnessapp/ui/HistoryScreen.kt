package com.example.fitnessapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessapp.R
import com.example.fitnessapp.extensions.toFormattedDateTime
import com.example.fitnessapp.uielement.FScaffold
import com.example.fitnessapp.uielement.theme.FitnessAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBackPressed: () -> Unit,
    onItemClick: (Long) -> Unit,
) {
    val history by viewModel.historyFlow.collectAsStateWithLifecycle(emptyList())

    FScaffold(
        title = stringResource(R.string.history_screen_name),
        showNavigationIcon = true,
        onClick = onBackPressed,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    items(
                        count = history.size,
                        key = { index ->
                            history[index].activityId
                        }
                    ) { index ->
                        val historyItem = history[index]
                        OutlinedCard(
                            onClick = { onItemClick.invoke(historyItem.activityId) },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                            ) {
                                HistoryItem(
                                    data = historyItem.activityId.toString()
                                )
                                HistoryItem(
                                    data = stringResource(
                                        R.string.start_time,
                                        historyItem.startTime.toFormattedDateTime()
                                    )
                                )
                                HistoryItem(
                                    data = stringResource(
                                        R.string.total_distance_text_label_unit_of_measure,
                                        historyItem.distance
                                    )
                                )
                                HistoryItem(
                                    data = historyItem.duration.toComponents { hours, minutes, seconds, _ ->
                                        stringResource(
                                            R.string.duration_text_label_units_of_measure,
                                            hours,
                                            minutes,
                                            seconds
                                        )
                                    }
                                )
                                HistoryItem(
                                    data = stringResource(
                                        R.string.average_speed_text_label_unit_of_measure,
                                        historyItem.averageSpeed
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun HistoryItem(
    data: String,
    style: TextStyle = FitnessAppTheme.typography.displayMedium
) {
    Text(
        text = data,
        style = style
    )
    Spacer(modifier = Modifier.height(4.dp))
}
