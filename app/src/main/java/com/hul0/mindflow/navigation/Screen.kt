package com.hul0.mindflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Quotes : Screen("quotes", "Quotes", Icons.Default.FormatQuote)
    object MoodTracker : Screen("mood_tracker", "Mood", Icons.Default.Face)
    object Meditation : Screen("meditation", "Meditate", Icons.Default.SelfImprovement)
}
