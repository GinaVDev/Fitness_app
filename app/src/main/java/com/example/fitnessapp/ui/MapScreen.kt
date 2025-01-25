package com.example.fitnessapp.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessapp.R
import com.example.fitnessapp.data.LocationService
import com.example.fitnessapp.repository.model.CurrentLocation
import com.example.fitnessapp.repository.model.DistanceAndDuration
import com.example.fitnessapp.repository.model.FitnessActivityState
import com.example.fitnessapp.repository.model.SpeedInformation
import com.example.fitnessapp.uielement.FIconButton
import com.example.fitnessapp.uielement.FIconType
import com.example.fitnessapp.uielement.FScaffold
import com.example.fitnessapp.uielement.FText
import com.example.fitnessapp.uielement.theme.FitnessAppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.time.Duration

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    currentLocation: CurrentLocation?,
    onBackPressed: () -> Unit,
    navigateToSummaryScreen: () -> Unit,
) {
    val distanceAndDuration by viewModel.distanceAndDuration.collectAsStateWithLifecycle(
        initialValue = DistanceAndDuration(distance = 0f, duration = Duration.ZERO)
    )

    val speedInformation by viewModel.speedInformation.collectAsStateWithLifecycle(
        initialValue = SpeedInformation(speed = 0f, tempo = 0f, averageSpeed = 0f)
    )

    val route by viewModel.route.collectAsStateWithLifecycle(emptyList())

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(47.4983, 19.0408), ZOOM)
    }

    val fitnessActivityState by viewModel.fitnessActivityState.collectAsStateWithLifecycle(
        FitnessActivityState.NOTRUNNING
    )

    val context = LocalContext.current

    LaunchedEffect(route, currentLocation) {
        Log.d("camerapos", "LE is run")
        if (fitnessActivityState == FitnessActivityState.NOTRUNNING) {
            currentLocation?.let { location ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    ZOOM
                )
            }
        } else {
            route.lastOrNull()?.let { location ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    ZOOM
                )
            }
        }
    }

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            Log.d("camerapos", "$event")
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    FScaffold(
        title = stringResource(R.string.map_screen_name),
        showNavigationIcon = true,
        onClick = onBackPressed,
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    GoogleMap(
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true),
                    ) {
                        Polyline(
                            points = route
                        )
                    }

                    when (fitnessActivityState) {
                        FitnessActivityState.NOTRUNNING -> {
                            Log.d("FitnessState", "State is NOTRUNNING")
                            PlayButtonOnTheMap(
                                onClickStart = {
                                    startLocationService(context)
                                }
                            )
                        }

                        FitnessActivityState.RUNNING -> {
                            Log.d("FitnessState", "State is RUNNING")
                            PauseButtonOnTheMap(
                                onClickPause = {
                                    pause(context)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        FitnessActivityState.PAUSED -> {
                            Log.d("FitnessState", "State is PAUSED")
                            PlayAndStopButtonOnTheMap(
                                onClickStart = {
                                    startAfterPause(context)
                                },
                                onClickStop = {
                                    stopLocationService(context)
                                    navigateToSummaryScreen()
                                }
                            )
                        }
                    }
                }
                when (fitnessActivityState) {
                    FitnessActivityState.RUNNING -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        FitnessActivityData(
                            modifier = Modifier,
                            distanceAndDuration = distanceAndDuration,
                            speedInformation = speedInformation
                        )
                    }
                    FitnessActivityState.PAUSED -> {
                        // no-op
                    }

                    FitnessActivityState.NOTRUNNING -> {
                        // no-op
                    }
                }
            }
        }
    )
}

@Composable
fun PauseButtonOnTheMap(
    modifier: Modifier = Modifier,
    onClickPause: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                PauseButton(
                    onClickPause = onClickPause
                )
            }
        }
    }
}

@Composable
fun PlayAndStopButtonOnTheMap(
    modifier: Modifier = Modifier,
    onClickStart: () -> Unit,
    onClickStop: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                PlayButton(
                    onClickStart = onClickStart
                )
                Spacer(modifier = modifier.width(8.dp))
                StopButton(
                    onClickStop = onClickStop
                )
            }
        }
    }
}

@Composable
fun PlayButtonOnTheMap(
    modifier: Modifier = Modifier,
    onClickStart: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                PlayButton(
                    onClickStart = onClickStart
                )
            }
        }
    }
}

@Composable
fun FitnessActivityData(
    modifier: Modifier,
    distanceAndDuration: DistanceAndDuration,
    speedInformation: SpeedInformation
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column {
            FText(
                text = stringResource(
                    R.string.total_distance_text_label_unit_of_measure,
                    distanceAndDuration.distance
                ),
                style = FitnessAppTheme.typography.displayMedium
            )
            FText(
                text = stringResource(
                    R.string.speed_text_label_unit_of_measuer,
                    speedInformation.speed
                ),
                style = FitnessAppTheme.typography.displayMedium
            )
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        Column {
            FText(
                text = distanceAndDuration.duration.toComponents { hours, minutes, seconds, _ ->
                    stringResource(
                        R.string.duration_text_label_units_of_measure,
                        hours,
                        minutes,
                        seconds
                    )
                },
                style = FitnessAppTheme.typography.displayMedium
            )

            val tempo = if (speedInformation.tempo > 200) {
                stringResource(R.string.tempo_no_data)
            } else {
                stringResource(
                    R.string.tempo_text_unit_of_measure,
                    speedInformation.tempo
                )
            }
            FText(
                text = tempo,
                style = FitnessAppTheme.typography.displayMedium
            )
        }
    }
}

@Composable
fun StopButton(
    onClickStop: () -> Unit
) {
    FIconButton(
        onClick = onClickStop,
        modifier = Modifier
            .size(72.dp),
        iconType = FIconType.Drawable(icon = R.drawable.stop),
        colors = IconButtonColors(
            contentColor = FitnessAppTheme.colorScheme.onPrimary,
            containerColor = FitnessAppTheme.colorScheme.stopButton,
            disabledContentColor = FitnessAppTheme.colorScheme.onPrimary,
            disabledContainerColor = FitnessAppTheme.colorScheme.stopButton
        )
    )
}

@Composable
fun PlayButton(
    onClickStart: () -> Unit
) {
    FIconButton(
        onClick = onClickStart,
        modifier = Modifier
            .size(72.dp),
        iconType = FIconType.Drawable(icon = R.drawable.play_arrow_24px),
        colors = IconButtonColors(
            contentColor = FitnessAppTheme.colorScheme.onPrimary,
            containerColor = FitnessAppTheme.colorScheme.playButton,
            disabledContentColor = FitnessAppTheme.colorScheme.onPrimary,
            disabledContainerColor = FitnessAppTheme.colorScheme.playButton
        )
    )
}

@Composable
fun PauseButton(
    onClickPause: () -> Unit,
) {
    FIconButton(
        onClick = onClickPause,
        modifier = Modifier
            .size(72.dp),
        iconType = FIconType.Drawable(icon = R.drawable.pause_24px),
        colors = IconButtonColors(
            contentColor = FitnessAppTheme.colorScheme.onPrimary,
            containerColor = FitnessAppTheme.colorScheme.primary,
            disabledContentColor = FitnessAppTheme.colorScheme.onPrimary,
            disabledContainerColor = FitnessAppTheme.colorScheme.primary
        )
    )
}

private fun startLocationService(context: Context) {
    Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_START
        context.startService(this)
    }
}

private fun stopLocationService(context: Context) {
    Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP
        context.startService(this)
    }
}

private fun pause(context: Context) {
    Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_PAUSE
        context.startService(this)
    }
}

private fun startAfterPause(context: Context) {
    Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_STARTAFTERPAUSE
        context.startService(this)
    }
}

const val ZOOM = 16f
