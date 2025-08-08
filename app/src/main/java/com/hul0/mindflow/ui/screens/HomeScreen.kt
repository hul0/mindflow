// app/src/main/java/com/hul0/mindflow/ui/screens/HomeScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hul0.mindflow.navigation.Screen
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val features = listOf(
        Feature("Mood Tracker", Screen.MoodTracker.route, Icons.Default.SentimentSatisfied, Color(0xFF6C5CE7), "Track emotions"),
        Feature("To-Do List", Screen.Todo.route, Icons.Default.Checklist, Color(0xFF00B894), "Stay organized"),
        Feature("Journal", Screen.Journal.route, Icons.Default.Book, Color(0xFFE17055), "Your thoughts"),
        Feature("Sleep Tracker", Screen.SleepTracker.route, Icons.Default.Bedtime, Color(0xFF8B5CF6), "Better rest"),
        Feature("Breathwork", Screen.Breathwork.route, Icons.Default.Spa, Color(0xFF10B981), "Find your calm"),
        Feature("Mental Health", Screen.MentalHealthTips.route, Icons.Default.Lightbulb, Color(0xFFFDCB6E), "Wellness tips"),
        Feature("Fun Facts", Screen.FunFacts.route, Icons.Default.Celebration, Color(0xFFFF7675), "Discover wonder"),
        Feature("BMI Calculator", Screen.BmiCalculator.route, Icons.Default.Calculate, Color(0xFF636E72), "Health insights"),
        Feature("Meditation", Screen.Meditation.route, Icons.Default.SelfImprovement, Color(0xFFE84393), "Inner peace"),
        Feature("Quotes", Screen.Quotes.route, Icons.Default.FormatQuote, Color(0xFF00CEC9), "Daily inspiration")
    )

    val calendar = Calendar.getInstance()
    val greetings = when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning! ☀️"
        in 12..16 -> "Good Afternoon! 🌤️"
        in 17..20 -> "Good Evening! 🌆"
        else -> "Night Owl! 🦉"
    }
    val date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            HeaderSection(greetings, date)
            Spacer(modifier = Modifier.height(32.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.heightIn(max = 1000.dp) // Avoid nested scrolling issues
            ) {
                items(features) { feature ->
                    FeatureCard(feature = feature) {
                        navController.navigate(feature.route)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(greeting: String, date: String) {
    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
fun FeatureCard(feature: Feature, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "cardScale"
    )

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = feature.color.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, feature.color.copy(alpha = 0.3f))
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .wrapContentSize(Alignment.Center),
                    tint = feature.color
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = feature.subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

data class Feature(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val color: Color,
    val subtitle: String
)
