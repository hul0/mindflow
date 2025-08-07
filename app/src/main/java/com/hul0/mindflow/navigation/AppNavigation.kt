package com.hul0.mindflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hul0.mindflow.ui.screens.HomeScreen
import com.hul0.mindflow.ui.screens.MeditationScreen
import com.hul0.mindflow.ui.screens.MoodTrackerScreen
import com.hul0.mindflow.ui.screens.QuotesScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Quotes.route) {
            QuotesScreen()
        }
        composable(Screen.MoodTracker.route) {
            MoodTrackerScreen()
        }
        composable(Screen.Meditation.route) {
            MeditationScreen()
        }
    }
}
