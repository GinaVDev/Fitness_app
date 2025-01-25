package com.example.fitnessapp.ui

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitnessapp.ObserveAsEvent
import com.example.fitnessapp.R
import com.example.fitnessapp.extensions.openSettings
import com.example.fitnessapp.repository.model.CurrentLocation
import com.example.fitnessapp.repository.model.FitnessActivityState
import com.example.fitnessapp.uielement.FAlertDialog
import com.example.fitnessapp.uielement.FButton
import com.example.fitnessapp.uielement.FScaffold
import com.example.fitnessapp.uielement.FText
import com.example.fitnessapp.uielement.theme.FitnessAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreen(
    viewModel: StartScreenViewModel,
    navigateToMap: (CurrentLocation) -> Unit,
    navigateToRoute: () -> Unit,
    navigateToHistoryScreen: () -> Unit
) {
    val context = LocalContext.current

    val showRationalAlert by viewModel.showRationalAlert.collectAsStateWithLifecycle()
    val showGpsAlert by viewModel.showGpsAlert.collectAsStateWithLifecycle()
    val currentLocation by viewModel.location.collectAsStateWithLifecycle()
    val lastLocation by viewModel.lastLocation.collectAsStateWithLifecycle()

    val fitnessActivityState by viewModel.fitnessActivityState.collectAsStateWithLifecycle(
        FitnessActivityState.NOTRUNNING
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(LAT, LONG), ZOOM)
    }

    BackHandler {
        (context as? Activity)?.finish()
    }

    LaunchedEffect(lastLocation) {
        lastLocation?.let { lastLocation ->
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(LatLng(lastLocation.lat, lastLocation.lng), ZOOMSTART)
        } ?: run {
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(LatLng(LAT, LONG), ZOOM)
        }
    }

    ObserveAsEvent(flow = viewModel.navigateToMapEvent) {
        currentLocation?.let { location ->
            navigateToMap.invoke(location)
        }
    }

    FScaffold(
        title = stringResource(R.string.start_screen_name),
        showNavigationIcon = false,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(FitnessAppTheme.colorScheme.primaryContainer)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(ALPHA)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GoogleMap(
                        modifier = Modifier
                            .clip(CircleShape)
                            .fillMaxWidth(FRACTION)
                            .aspectRatio(RATIO),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = false)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        MapAndHistoryButtons(
                            onClickMap = navigateToRoute,
                            onClickHistory = navigateToHistoryScreen,
                            enabledMap = (
                                fitnessActivityState == FitnessActivityState.RUNNING ||
                                    fitnessActivityState == FitnessActivityState.PAUSED
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                    StartButton(
                        onClick = {
                            viewModel.handleLocationPermission()
                            viewModel.fetchCurrentLocation()
                        },
                        isEnabled = (fitnessActivityState == FitnessActivityState.NOTRUNNING),
                        showRationalAlert = showRationalAlert,
                        showGpsAlert = showGpsAlert,
                        viewModel = viewModel
                    )
                }
            }
        }
    )
}

@Composable
fun StartButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    showRationalAlert: Boolean,
    showGpsAlert: Boolean,
    viewModel: StartScreenViewModel
) {
    val context = LocalContext.current

    FButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        enabled = isEnabled,
        onClick = onClick
    ) {
        FText(
            text = stringResource(R.string.start_button_name),
            style = FitnessAppTheme.typography.labelSmall
        )
    }
    if (showRationalAlert) {
        RationalAlert(
            onConfirm = {
                context.openSettings()
                viewModel.hideRationalAlert()
            },
            onCancel = { viewModel.hideRationalAlert() }
        )
    }
    if (showGpsAlert) {
        GpsAlert(
            onConfirm = {
                val locationSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(locationSettingsIntent)
                viewModel.hideGpsAlert()
            },
            onCancel = { viewModel.hideGpsAlert() }
        )
    }
}

@Composable
fun MapAndHistoryButtons(
    onClickMap: () -> Unit,
    onClickHistory: () -> Unit,
    enabledMap: Boolean,
) {
    FButton(
        modifier = Modifier,
        enabled = enabledMap,
        onClick = onClickMap
    ) {
        FText(
            text = stringResource(R.string.map_button),
            style = FitnessAppTheme.typography.labelSmall
        )
    }
    Spacer(modifier = Modifier.width(32.dp))
    FButton(
        modifier = Modifier,
        onClick = onClickHistory
    ) {
        FText(
            text = stringResource(R.string.history_button),
            style = FitnessAppTheme.typography.labelSmall
        )
    }
}

@Composable
fun GpsAlert(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    FAlertDialog(
        modifier = modifier,
        title = stringResource(R.string.alert_dialog_gps_title),
        message = stringResource(R.string.alert_dialog_gps_text),
        confirmButtonText = stringResource(R.string.alert_dialog_confirm_button),
        cancelButtonText = stringResource(R.string.alert_dialog_cancel_button),
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}

@Composable
fun RationalAlert(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    FAlertDialog(
        title = stringResource(R.string.alert_dialog_location_permission_title),
        message = stringResource(R.string.alert_dialog_location_permission_text),
        confirmButtonText = stringResource(R.string.alert_dialog_confirm_button),
        cancelButtonText = stringResource(R.string.alert_dialog_cancel_button),
        onConfirm = onConfirm,
        onCancel = onCancel,
    )
}

const val LAT = 47.4983
const val LONG = 19.0408
const val ALPHA = 0.6f
const val FRACTION = 0.75f
const val RATIO = 1f
const val ZOOMSTART = 12f
