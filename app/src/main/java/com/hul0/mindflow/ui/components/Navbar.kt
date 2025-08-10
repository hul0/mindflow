package com.hul0.mindflow.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hul0.mindflow.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Quotes,
        Screen.MoodTracker,
        Screen.Meditation,
        Screen.Profile,
        Screen.Chat
    )

    // A Box that provides padding around the navbar.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // The main container Surface. It clips the content, draws the border,
        // and provides the main background color.
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
            // Layer 1: The main background color of the navigation bar.
            color = MaterialTheme.colorScheme.surface
        ) {
            // The content (icons and text).
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    // Layer 2: A very subtle overlay on top of the Surface color
                    // to help separate the items from the background.
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    BottomNavItem(
                        screen = screen,
                        isSelected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun BottomNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "navItemScale"
    )

    val itemColor by animateColorAsState(
        targetValue = if (isSelected) getNavItemColor(screen) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "itemColor"
    )

    val indicatorColor by animateColorAsState(
        targetValue = if (isSelected) getNavItemColor(screen).copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "indicatorColor"
    )

    Box(
        modifier = Modifier
            .size(52.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(indicatorColor)
            .selectable(
                selected = isSelected,
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    isPressed = true
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                modifier = Modifier.size(22.dp),
                tint = itemColor
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = screen.title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = itemColor,
                letterSpacing = 0.3.sp,
                maxLines = 1
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

@Composable
private fun getNavItemColor(screen: Screen): Color {
    return when (screen.route) {
        "home" -> Color(0xFF3B82F6)
        "quotes" -> Color(0xFF10B981)
        "mood_tracker" -> Color(0xFF8B5CF6)
        "chat" -> Color(0xFFFFA900)
        "meditation" -> Color(0xFFEC4899)
        "profile" -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.primary
    }
}
