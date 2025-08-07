package com.hul0.mindflow.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hul0.mindflow.navigation.Screen
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Quotes,
        Screen.MoodTracker,
        Screen.Meditation
    )

    // Floating animation for the nav bar
    val infiniteTransition = rememberInfiniteTransition(label = "navFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "floatOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Glassmorphism background with subtle animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E).copy(alpha = 0.95f + 0.05f * sin(floatOffset * Math.PI).toFloat()),
                            Color(0xFF16213E).copy(alpha = 0.9f + 0.1f * sin(floatOffset * Math.PI * 0.8).toFloat())
                        )
                    )
                )
        ) {
            // Subtle glow effect
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            radius = 200f
                        )
                    )
            )
        }

        // Navigation items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { screen ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                EnhancedNavItem(
                    screen = screen,
                    isSelected = isSelected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EnhancedNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    // Scale animation for press
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.85f
            isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "navItemScale"
    )

    // Color animations
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "iconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "textColor"
    )

    // Background glow for selected item
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.3f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "glowAlpha"
    )

    // Floating animation for selected item
    val infiniteTransition = rememberInfiniteTransition(label = "selectedFloat")
    val selectedFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isSelected) 3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "selectedFloat"
    )

    // Pulse effect for selected icon
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "iconScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .scale(scale)
            .offset(y = (-selectedFloat).dp)
            .selectable(
                selected = isSelected,
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = true
                onClick()
            }
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        // BUG FIX: Wrap the Icon and its Glow effect in a single Box to layer them.
        // This prevents the glow from pushing the icon and text down, fixing the layout.
        Box(contentAlignment = Alignment.Center) {
            // Glow background for selected item
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    getNavItemColor(screen).copy(alpha = glowAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    // Inner glow
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }

            // Icon with enhanced effects
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                modifier = Modifier
                    .size(26.dp)
                    .scale(iconScale)
                    .graphicsLayer {
                        // Subtle rotation for selected item
                        rotationZ = if (isSelected) sin(selectedFloat * 0.1f) * 2f else 0f
                    },
                tint = iconColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Enhanced text with better typography
        Text(
            text = screen.title,
            fontSize = if (isSelected) 12.sp else 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            letterSpacing = 0.3.sp
        )

        // Active indicator dot
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(getNavItemColor(screen))
                    .scale(iconScale * 0.8f)
            )
        }
    }

    // Reset press state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

// Color mapping for each nav item
@Composable
private fun getNavItemColor(screen: Screen): Color {
    return when (screen.route) {
        "home" -> Color(0xFF74B9FF)
        "quotes" -> Color(0xFF00CEC9)
        "mood_tracker" -> Color(0xFF6C5CE7)
        "meditation" -> Color(0xFFE84393)
        else -> Color(0xFF74B9FF)
    }
}