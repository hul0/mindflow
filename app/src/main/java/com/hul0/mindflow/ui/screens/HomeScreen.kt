// app/src/main/java/com/hul0/mindflow/ui/screens/HomeScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hul0.mindflow.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    val features = listOf(
        "Mood Tracker" to Screen.MoodTracker.route,
        "To-Do List" to Screen.Todo.route,
        "Journal" to Screen.Journal.route,
        "Sleep Tracker" to Screen.SleepTracker.route,
        "Breathwork" to Screen.Breathwork.route,
        "Mental Health Tips" to Screen.MentalHealthTips.route,
        "Fun Facts" to Screen.FunFacts.route,
        "BMI Calculator" to Screen.BmiCalculator.route,
        "Meditation" to Screen.Meditation.route,
        "Quotes" to Screen.Quotes.route,
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(features) { (title, route) ->
            FeatureCard(title = title) {
                navController.navigate(route)
            }
        }
    }
}

@Composable
fun FeatureCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }
    }
}
