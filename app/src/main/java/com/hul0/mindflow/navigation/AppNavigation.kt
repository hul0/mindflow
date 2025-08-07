// app/src/main/java/com/hul0/mindflow/navigation/AppNavigation.kt
package com.hul0.mindflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hul0.mindflow.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Quotes.route) { QuotesScreen() }
        composable(Screen.Meditation.route) { MeditationScreen() }
        composable(Screen.MoodTracker.route) { MoodTrackerScreen() }
        composable(Screen.Todo.route) { TodoScreen() }
        composable(Screen.SleepTracker.route) { SleepTrackerScreen() }
        composable(Screen.Journal.route) { JournalScreen() }
        composable(Screen.FunFacts.route) { FunFactsScreen() }
        composable(Screen.MentalHealthTips.route) { MentalHealthTipsScreen() }
        composable(Screen.BmiCalculator.route) { BmiCalculatorScreen() }
        composable(Screen.Breathwork.route) { BreathworkScreen() }
    }
}
