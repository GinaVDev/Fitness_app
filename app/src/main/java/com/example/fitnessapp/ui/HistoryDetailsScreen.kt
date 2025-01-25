package com.example.fitnessapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.LineType
import com.example.fitnessapp.R
import com.example.fitnessapp.uielement.FScaffold
import com.example.fitnessapp.uielement.theme.FitnessAppTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HistoryDetailsScreen(
    viewModel: HistoryDetailsViewModel,
    activityId: Long,
    onBackPressed: () -> Unit,
) {
    val historyDetails by viewModel.historyDetailsFlow.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), ZOOM)
    }

    var isFullScreen by remember { mutableStateOf(false) }

    LaunchedEffect(historyDetails.route) {
        val coordinates = historyDetails.route
        if (coordinates.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            coordinates.forEach { coordinate ->
                boundsBuilder.include(coordinate)
            }
            val bounds = boundsBuilder.build()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50)
            cameraPositionState.move(cameraUpdate)
        }
    }

    LaunchedEffect(activityId) {
        viewModel.getHistoryDetails(activityId)
    }

    FScaffold(
        title = stringResource(R.string.history_details_screen_name),
        showNavigationIcon = true,
        onClick = onBackPressed,
        content = { paddingValues ->
            if (!isFullScreen) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    ) {
                        Map(
                            route = historyDetails.route,
                            cameraPositionState = cameraPositionState,
                            onMapClick = { isFullScreen = true }
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(CHARTSIZE)
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.avg_speed_chart_label),
                                style = FitnessAppTheme.typography.labelSmall
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                            )
                            AvgSpeedChart(
                                viewModel = viewModel,
                                points = historyDetails.averageSpeedPerKmList,
                                lineType = LineType.SmoothCurve()
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                            )
                            Text(
                                text = stringResource(R.string.elevation_chart_label),
                                style = FitnessAppTheme.typography.labelSmall
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                            )
                            AltitudeChart(
                                viewModel = viewModel,
                                points = historyDetails.altitudeList,
                                lineType = LineType.Straight()
                            )
                        }
                    }
                }
            } else {
                FullScreenMap(
                    onDismiss = { isFullScreen = false },
                    cameraPositionState = cameraPositionState,
                    routeList = historyDetails.route
                )
            }
        }
    )
}

@Composable
fun AvgSpeedChart(
    viewModel: HistoryDetailsViewModel,
    points: List<Float>,
    lineType: LineType
) {
    val context = LocalContext.current
    val colorPrimary = FitnessAppTheme.colorScheme.primary
    val colorInversePrimary = FitnessAppTheme.colorScheme.inversePrimary
    val avgSpeedChartData by viewModel.avgSpeedChartData.collectAsState()

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            viewModel.calculateAvgSpeedChartData(
                points.mapIndexed { index, value ->
                    Point(
                        x = (index + 1).toFloat(),
                        y = value
                    )
                },
                context,
                colorPrimary,
                colorInversePrimary,
                lineType
            )
        }
    }

    avgSpeedChartData?.let { data ->
        LineChart(modifier = Modifier.fillMaxWidth(), lineChartData = data.lineChartData)
    }
}

@Composable
fun AltitudeChart(
    viewModel: HistoryDetailsViewModel,
    points: List<Double>,
    lineType: LineType
) {
    val context = LocalContext.current
    val colorPrimary = FitnessAppTheme.colorScheme.primary
    val colorInversePrimary = FitnessAppTheme.colorScheme.inversePrimary
    val altitudeChartData by viewModel.altitudeChartData.collectAsState()

    LaunchedEffect(points) {
        if (points.isNotEmpty()) {
            viewModel.calculateAltitudeChartData(
                points.mapIndexed { index, value ->
                    Point(
                        x = (index + 1).toFloat(),
                        y = value.toFloat()
                    )
                },
                context,
                colorPrimary,
                colorInversePrimary,
                lineType
            )
        }
    }

    altitudeChartData?.let { data ->
        LineChart(modifier = Modifier.fillMaxWidth(), lineChartData = data.lineChartData)
    }
}

@Composable
fun Map(
    route: List<LatLng>,
    cameraPositionState: CameraPositionState,
    onMapClick: () -> Unit
) {
    if (route.isNotEmpty()) {
        val markerStart = route.first()
        val markerStop = route.last()

        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapClick = {
                onMapClick()
            }
        ) {
            Polyline(points = route)
            Marker(
                state = MarkerState(position = markerStart),
                title = stringResource(R.string.start_point_label)
            )
            Marker(
                state = MarkerState(position = markerStop),
                title = stringResource(R.string.stop_point_label)
            )
        }
    }
}

@Composable
fun FullScreenMap(
    onDismiss: () -> Unit,
    cameraPositionState: CameraPositionState,
    routeList: List<LatLng>,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = FitnessAppTheme.colorScheme.onSecondary
                        )
                    }
                }
                Map(
                    route = routeList,
                    cameraPositionState = cameraPositionState,
                    onMapClick = {
                        onDismiss()
                    }
                )
            }
        }
    }
}

const val CHARTSIZE = 2f
