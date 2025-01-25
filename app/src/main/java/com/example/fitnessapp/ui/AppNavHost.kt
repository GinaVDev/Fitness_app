package com.example.fitnessapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.fitnessapp.repository.model.CurrentLocation
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppNavHost(
    startDestination: String,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Start.route) {
            val viewModel: StartScreenViewModel = hiltViewModel()
            StartScreen(
                viewModel = viewModel,
                navigateToMap = { currentLocation ->
                    navController.navigate(
                        MapRoute(
                            latLng = currentLocation,
                        )
                    )
                },
                navigateToHistoryScreen = {
                    navController.navigate(Screen.History.route)
                },
                navigateToRoute = {
                    navController.navigate(
                        MapRoute(
                            latLng = null
                        )
                    )
                }
            )
        }

        composable<MapRoute>(
            typeMap = mapOf(
                typeOf<CurrentLocation?>() to CustomNavType.location,
            )
        ) {
            val arguments = it.toRoute<MapRoute>()
            val viewModel: MapViewModel = hiltViewModel()
            MapScreen(
                viewModel = viewModel,
                currentLocation = arguments.latLng,
                onBackPressed = {
                    navController.navigateUp()
                },
                navigateToSummaryScreen = {
                    navController.navigate(Screen.SummaryScreen.route)
                }
            )
        }

        composable(route = Screen.SummaryScreen.route) {
            val viewModel: SummaryViewModel = hiltViewModel()
            SummaryScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.navigate(Screen.Start.route)
                }
            )
        }
        composable(route = Screen.History.route) {
            val viewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navController.navigateUp()
                },
                onItemClick = { activityId ->
                    navController.navigate(Screen.HistoryDetails.route + "/$activityId")
                }
            )
        }

        composable(
            route = Screen.HistoryDetails.route + "/{activityId}",
            arguments = listOf(
                navArgument("activityId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val activityId: Long =
                backStackEntry.arguments?.getLong("activityId") ?: 0L
            val viewModel: HistoryDetailsViewModel = hiltViewModel()
            HistoryDetailsScreen(
                viewModel = viewModel,
                activityId = activityId,
                onBackPressed = {
                    navController.navigateUp()
                },
            )
        }
    }
}

@Serializable
data object StartRoute

@Serializable
data class MapRoute(
    val latLng: CurrentLocation?
)
