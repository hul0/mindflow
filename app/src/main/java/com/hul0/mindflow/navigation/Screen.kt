// app/src/main/java/com/hul0/mindflow/navigation/Screen.kt
package com.hul0.mindflow.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quotes : Screen("quotes")
    object Meditation : Screen("meditation")
    object MoodTracker : Screen("mood_tracker")
    object Todo : Screen("todo")
    object SleepTracker : Screen("sleep_tracker")
    object Journal : Screen("journal")
    object FunFacts : Screen("fun_facts")
    object MentalHealthTips : Screen("mental_health_tips")
    object BmiCalculator : Screen("bmi_calculator")
    object Breathwork : Screen("breathwork")
}