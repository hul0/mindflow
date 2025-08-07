// app/src/main/java/com/hul0/mindflow/ui/screens/HomeScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hul0.mindflow.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val features = listOf(
        Feature("Mood Tracker", Screen.MoodTracker.route, Icons.Default.SentimentSatisfied, listOf(Color(0xFFF9C8D9), Color(0xFFF3A6C2))),
        Feature("To-Do List", Screen.Todo.route, Icons.Default.Checklist, listOf(Color(0xFFC8E6C9), Color(0xFFA5D6A7))),
        Feature("Journal", Screen.Journal.route, Icons.Default.Book, listOf(Color(0xFFB3E5FC), Color(0xFF81D4FA))),
        Feature("Sleep Tracker", Screen.SleepTracker.route, Icons.Default.Bedtime, listOf(Color(0xFFC5CAE9), Color(0xFF9FA8DA))),
        Feature("Breathwork", Screen.Breathwork.route, Icons.Default.Spa, listOf(Color(0xFFB2DFDB), Color(0xFF80CBC4))),
        Feature("Mental Health Tips", Screen.MentalHealthTips.route, Icons.Default.Lightbulb, listOf(Color(0xFFFFF9C4), Color(0xFFFFF59D))),
        Feature("Fun Facts", Screen.FunFacts.route, Icons.Default.Celebration, listOf(Color(0xFFFFCCBC), Color(0xFFFFAB91))),
        Feature("BMI Calculator", Screen.BmiCalculator.route, Icons.Default.Calculate, listOf(Color(0xFFD7CCC8), Color(0xFFBCAAA4))),
        Feature("Meditation", Screen.Meditation.route, Icons.Default.SelfImprovement, listOf(Color(0xFFE1BEE7), Color(0xFFCE93D8))),
        Feature("Quotes", Screen.Quotes.route, Icons.Default.FormatQuote, listOf(Color(0xFFF0F4C3), Color(0xFFE6EE9C)))
    )

    val calendar = Calendar.getInstance()
    val greeting = when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    val date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(features) { feature ->
                FeatureCard(feature = feature) {
                    navController.navigate(feature.route)
                }
            }
        }
    }
}

@Composable
fun FeatureCard(feature: Feature, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(feature.gradientColors))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

data class Feature(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val gradientColors: List<Color>
)