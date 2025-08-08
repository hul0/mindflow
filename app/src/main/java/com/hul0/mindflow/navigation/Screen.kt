package com.hul0.mindflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Quotes : Screen("quotes", "Quotes", Icons.Default.FormatQuote)
    object Meditation : Screen("meditation", "Meditate", Icons.Default.SelfImprovement)
    object MoodTracker : Screen("mood_tracker", "Moods", Icons.Default.SentimentSatisfied)
    object Todo : Screen("todo", "To-Do", Icons.Default.Checklist)
    object SleepTracker : Screen("sleep_tracker", "Sleep", Icons.Default.Bedtime)
    object Journal : Screen("journal", "Journal", Icons.Default.Book)
    object FunFacts : Screen("fun_facts", "Facts", Icons.Default.Celebration)
    object MentalHealthTips : Screen("mental_health_tips", "Tips", Icons.Default.Lightbulb)
    object BmiCalculator : Screen("bmi_calculator", "BMI", Icons.Default.Calculate)
    object Breathwork : Screen("breathwork", "Breathe", Icons.Default.Spa)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}
