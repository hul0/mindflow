// app/src/main/java/com/hul0/mindflow/ui/screens/HomeScreen.kt
package com.hul0.mindflow.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
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
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val day = when(calendar.get(Calendar.DAY_OF_WEEK)){
        1 -> "Sunday"
        2 -> "Monday"
        3 -> "Tuesday"
        4 -> "Wednesday"
        5 -> "Thursday"
        6 -> "Friday"
        7 -> "Saturday"
        else -> ""
    }

    val greetings = when (hour) {
        0 -> if (minute < 30) "Happy $day 🦉" else "Deep in the quiet of the night. 🌃"
        1 -> if (minute < 30) "The world is asleep, but you're still at it! ✨" else "Still going strong in the wee hours. 💪"
        2 -> if (minute < 30) "Embracing the silence of 2 AM. 🤫" else "The moon is your companion tonight. 🌔"
        3 -> if (minute < 30) "Welcome to the witching hour! 🧙‍♀️" else "The quietest time of the night. 🌌"
        4 -> if (minute < 30) "The birds will be singing soon. 🐦" else "Just before the dawn. 🌅"
        5 -> if (minute < 30) "The very first light of day. Good morning! 🌄" else "Early bird gets the worm! 🐛"
        6 -> if (minute < 30) "The sun is rising! ☀️" else "A fresh start to your day. ☕"
        7 -> if (minute < 30) "Good morning! Hope you have a great day. 😊" else "Time to get the day rolling! 🚀"
        8 -> if (minute < 30) "Morning rush hour! 🏃‍♂️" else "Hope your coffee is strong! ☕"
        9 -> if (minute < 30) "Time to be productive! 💻" else "Deep in the morning workflow. 📝"
        10 -> if (minute < 30) "Mid-morning focus time. 🎯" else "Keep up the great work! 👍"
        11 -> if (minute < 30) "Almost lunchtime! 😋" else "The morning is wrapping up. 🏁"
        12 -> if (minute < 30) "It's high noon! Good afternoon! 🕛" else "Time for a lunch break? 🥪"
        13 -> if (minute < 30) "Post-lunch productivity push! 💪" else "Beating the afternoon slump. ⚡"
        14 -> if (minute < 30) "Cruising through the afternoon. 🚗" else "Hope your afternoon is going smoothly. 🌤️"
        15 -> if (minute < 30) "Afternoon tea time? 🍵" else "The final stretch of the workday begins. 🏃‍♀️"
        16 -> if (minute < 30) "Wrapping up the day's main tasks. ✅" else "Almost time to clock out! 🕔"
        17 -> if (minute < 30) "Good evening! The day is winding down. 🌇" else "Time to relax and unwind. 😌"
        18 -> if (minute < 30) "The sun is setting. Beautiful evening! 🌆" else "What's for dinner tonight? �️"
        19 -> if (minute < 30) "Enjoy your evening! 🌃" else "Hope you're having a peaceful evening. 🙏"
        20 -> if (minute < 30) "Prime time! What are you watching? 📺" else "Settling in for the night. 🛋️"
        21 -> if (minute < 30) "Getting cozy for the evening. 🧣" else "The stars are coming out. ✨"
        22 -> if (minute < 30) "Time to start winding down for bed. 🥱" else "Hope you had a wonderful day. 💖"
        23 -> if (minute < 30) "Late night owl! 🦉" else "Almost a new day! 🌙"
        else -> "Hello there! 👋" // Fallback for any unexpected cases
    }
    val date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())
    val time = LocalTime.now()
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
            HeaderSection(greetings, date , day , time)
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
            Spacer(modifier = Modifier.size(55.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeaderSection(greeting: String, date: String , day: String , time: LocalTime) {
    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = "$day, $date",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
        Row {
            Icon(
                Icons.Default.AccessTime,
                contentDescription = "time",
                tint = Color(248, 224, 5, 255).copy(0.7f)
            )
            Spacer(Modifier.width(4.dp))
            Text("Time : ${time.hour}:${time.minute}",
                    style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ))
        }
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
        color = feature.color.copy(alpha = 0.05f),
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
