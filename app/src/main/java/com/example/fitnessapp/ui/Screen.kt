package com.example.fitnessapp.ui

sealed class Screen(open val route: String) {
    data object Start : Screen("startScreen")
    data object Map : Screen("mapScreen")
    data object SummaryScreen : Screen("summaryScreen")
    data object History : Screen("historyScreen")
    data object HistoryDetails : Screen("historyDetailsScreen")
}
