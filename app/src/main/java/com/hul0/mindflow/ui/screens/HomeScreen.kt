// app/src/main/java/com/hul0/mindflow/ui/screens/HomeScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlin.math.sin

@Composable
fun HomeScreen(navController: NavController) {
    val features = listOf(
        Feature("Mood Tracker", Screen.MoodTracker.route, Icons.Default.SentimentSatisfied,
            Color(0xFF6C5CE7), Color(0xFF74B9FF), "Track your emotions"),
        Feature("To-Do List", Screen.Todo.route, Icons.Default.Checklist,
            Color(0xFF00B894), Color(0xFF55A3FF), "Stay organized"),
        Feature("Journal", Screen.Journal.route, Icons.Default.Book,
            Color(0xFFE17055), Color(0xFFFFB8B8), "Your thoughts matter"),
        Feature("Sleep Tracker", Screen.SleepTracker.route, Icons.Default.Bedtime,
            Color(0xFF6C5CE7), Color(0xFFA29BFE), "Better rest awaits"),
        Feature("Breathwork", Screen.Breathwork.route, Icons.Default.Spa,
            Color(0xFF00B894), Color(0xFF7DFFDF), "Find your calm"),
        Feature("Mental Health Tips", Screen.MentalHealthTips.route, Icons.Default.Lightbulb,
            Color(0xFFFDCB6E), Color(0xFFFFE66D), "Wellness wisdom"),
        Feature("Fun Facts", Screen.FunFacts.route, Icons.Default.Celebration,
            Color(0xFFFF7675), Color(0xFFFD79A8), "Discover wonder"),
        Feature("BMI Calculator", Screen.BmiCalculator.route, Icons.Default.Calculate,
            Color(0xFF636E72), Color(0xFFB2BEC3), "Health insights"),
        Feature("Meditation", Screen.Meditation.route, Icons.Default.SelfImprovement,
            Color(0xFFE84393), Color(0xFFFD79A8), "Inner peace"),
        Feature("Quotes", Screen.Quotes.route, Icons.Default.FormatQuote,
            Color(0xFF00CEC9), Color(0xFF55EFC4), "Daily inspiration")
    )

    val calendar = Calendar.getInstance()
    val greetings = when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 5..8 -> listOf(
            "Rise & Shine! ☀️",
            "Morning, Champion!",
            "Wakey wakey! 🌅",
            "Good morning, Superstar!",
            "Hello, Beautiful Soul!",
            "Time to conquer today! 💪"
        )
        in 9..11 -> listOf(
            "Good Morning! 🌞",
            "Morning, Rockstar!",
            "Hello there, Awesome!",
            "Ready to slay today?",
            "Morning sunshine! ✨",
            "Let's make magic happen!"
        )
        in 12..13 -> listOf(
            "Good Afternoon! 🌤️",
            "Afternoon, Legend!",
            "Hope you're crushing it!",
            "Midday vibes! 🔥",
            "Afternoon, Warrior!",
            "Lunch break well-being!"
        )
        in 14..16 -> listOf(
            "Afternoon Delight! 🌻",
            "Hope your day rocks!",
            "Afternoon, Achiever!",
            "You're doing great! 🌟",
            "Afternoon check-in! 💫",
            "Keep being amazing!"
        )
        in 17..19 -> listOf(
            "Good Evening! 🌆",
            "Evening, Superstar!",
            "Hope you had a great day!",
            "Evening wind-down time!",
            "You made it! 🎉",
            "Evening, Champion!"
        )
        in 20..22 -> listOf(
            "Good Evening! 🌙",
            "Evening, Night Owl!",
            "Time to unwind! 🛋️",
            "Evening relaxation mode!",
            "You deserve some rest!",
            "Cozy evening vibes! ✨"
        )
        else -> listOf(
            "Night Owl Mode! 🦉",
            "Still up? You legend! 🌟",
            "Midnight wellness check! 🌙",
            "Late night, big dreams!",
            "Hello Batman",
            "Burning the midnight oil? 🕯️",
            "Night time = Me time!"
        )
    }

    // Random greeting selection with day-based seed for consistency
    val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
    val greeting = greetings[dayOfYear % greetings.size]
    val date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())

    // Animated background
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "backgroundOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E).copy(alpha = 0.95f + 0.05f * sin(backgroundOffset * Math.PI).toFloat()),
                        Color(0xFF16213E).copy(alpha = 0.9f + 0.1f * sin(backgroundOffset * Math.PI * 0.7).toFloat()),
                        Color(0xFF0F3460).copy(alpha = 0.85f + 0.15f * sin(backgroundOffset * Math.PI * 0.5).toFloat())
                    )
                )
            )
    ) {
        // Floating orbs for ambient effect
        repeat(3) { index ->
            FloatingOrb(
                modifier = Modifier.offset(
                    x = (50 + index * 120).dp,
                    y = (100 + index * 150).dp
                ),
                color = when (index) {
                    0 -> Color(0xFF6C5CE7).copy(alpha = 0.1f)
                    1 -> Color(0xFF00B894).copy(alpha = 0.08f)
                    else -> Color(0xFFE17055).copy(alpha = 0.06f)
                },
                delay = index * 2000L
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header with enhanced typography
            HeaderSection(greeting, date)

            Spacer(modifier = Modifier.height(32.dp))

            // Enhanced grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(features) { feature ->
                    EnhancedFeatureCard(feature = feature) {
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
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = date,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.7f),
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtle accent line
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF74B9FF),
                            Color(0xFF00B894)
                        )
                    )
                )
        )
    }
}

@Composable
fun EnhancedFeatureCard(feature: Feature, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "cardScale"
    )

    // Subtle pulse animation for icons
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "iconScale"
    )

    Box(
        modifier = Modifier
            .height(180.dp) // BUG FIX: Removed aspect ratio and set a fixed height
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        feature.primaryColor.copy(alpha = 0.9f),
                        feature.secondaryColor.copy(alpha = 0.7f)
                    ),
                    radius = 180f
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = true
                onClick()
            }
            .graphicsLayer {
                // Subtle 3D tilt effect
                rotationY = if (isPressed) 2f else 0f
                rotationX = if (isPressed) -2f else 0f
            },
        contentAlignment = Alignment.Center
    ) {
        // Background pattern overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.3f, 0.3f),
                        radius = 100f
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            // BUG FIX: Changed vertical arrangement to allow text to fit
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(36.dp) // BUG FIX: Reduced padding
        ) {
            // Enhanced icon with background
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Color.White.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .scale(iconScale),
                    tint = Color.White
                )
            }

            // BUG FIX: Removed fixed spacers to allow flexible layout
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = feature.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.3.sp,
                    maxLines = 1 // Ensure title doesn't wrap excessively
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = feature.subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.2.sp,
                    maxLines = 2 // Allow subtitle to wrap
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
fun FloatingOrb(
    modifier: Modifier = Modifier,
    color: Color,
    delay: Long = 0L
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000 + (delay / 10).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(delay.toInt())
        ), label = "orbFloat"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset((delay * 1.5).toInt())
        ), label = "orbScale"
    )

    Box(
        modifier = modifier
            .offset(y = offsetY.dp)
            .size(120.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color,
                        Color.Transparent
                    )
                )
            )
    )
}

data class Feature(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val subtitle: String
)