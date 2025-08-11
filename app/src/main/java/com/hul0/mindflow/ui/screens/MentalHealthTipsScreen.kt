// app/src/main/java/com/hul0/mindflow/ui/screens/MentalHealthTipsScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.ui.viewmodel.MentalHealthViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class WellnessCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val tips: List<String>
)

@Composable
fun MentalHealthTipsScreen(
    viewModel: MentalHealthViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val tip by viewModel.tip.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var currentTipIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // <<< CORRECT: Get a coroutine scope

    // Breathing animation
    val breathingScale by animateFloatAsState(
        targetValue = if (isVisible) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Rotation animation for refresh icon
    var rotationState by remember { mutableStateOf(0f) }
    val rotation by animateFloatAsState(
        targetValue = rotationState,
        animationSpec = tween(600, easing = EaseOutBack),
        label = "rotation"
    )

    // Wellness categories
    val wellnessCategories = remember {
        listOf(
            WellnessCategory(
                "Mindfulness",
                Icons.Rounded.SelfImprovement,
                Color(0xFF81C784),
                listOf("Practice deep breathing", "Try meditation", "Focus on present moment")
            ),
            WellnessCategory(
                "Exercise",
                Icons.AutoMirrored.Rounded.DirectionsRun,
                Color(0xFF64B5F6),
                listOf("Take a walk", "Stretch regularly", "Dance to music")
            ),
            WellnessCategory(
                "Social",
                Icons.Rounded.Groups,
                Color(0xFFFFB74D),
                listOf("Call a friend", "Join a community", "Share your feelings")
            ),
            WellnessCategory(
                "Rest",
                Icons.Rounded.Bedtime,
                Color(0xFFBA68C8),
                listOf("Get quality sleep", "Take breaks", "Practice relaxation")
            ),
            WellnessCategory(
                "Nutrition",
                Icons.Rounded.LocalFlorist,
                Color(0xFF4DB6AC),
                listOf("Eat mindfully", "Stay hydrated", "Enjoy healthy foods")
            ),
            WellnessCategory(
                "Creativity",
                Icons.Rounded.Palette,
                Color(0xFFF06292),
                listOf("Express yourself", "Try art therapy", "Write in a journal")
            )
        )
    }

    LaunchedEffect(Unit) {
        isVisible = true
        delay(500)
        viewModel.getNewTip()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.background,
                        colorScheme.surface.copy(alpha = 0.3f)
                    )
                )
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Header Section
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(800, easing = EaseOutCubic)
            ) + fadeIn(tween(800))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Wellness",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                    Text(
                        text = "Tips for your mind",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                // Mood indicator
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .scale(breathingScale)
                        .background(
                            color = colorScheme.primaryContainer.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEmotions,
                        contentDescription = "Mood",
                        tint = colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Categories Row
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(1000, delayMillis = 200))
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                items(wellnessCategories) { category ->
                    WellnessCategoryChip(
                        category = category,
                        colorScheme = colorScheme
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Tip Card
        AnimatedContent(
            targetState = tip?.tip ?: "Preparing your wellness tip...",
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { if (currentTipIndex % 2 == 0) it else -it },
                    animationSpec = tween(600, easing = EaseOutCubic)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { if (currentTipIndex % 2 == 0) -it else it },
                    animationSpec = tween(400, easing = EaseInCubic)
                )
            },
            label = "tip_transition",
            modifier = Modifier.weight(1f)
        ) { tipText ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = colorScheme.outline.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface.copy(alpha = 0.05f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Quote icon
                    Icon(
                        imageVector = Icons.Rounded.FormatQuote,
                        contentDescription = null,
                        tint = colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(32.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = tipText,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = colorScheme.onSurface,
                        lineHeight = 32.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (index == currentTipIndex % 5)
                                            colorScheme.primary
                                        else
                                            colorScheme.outline.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons Row
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                animationSpec = tween(600, delayMillis = 400, easing = EaseOutBack)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                // Favorite Button
                OutlinedButton(
                    onClick = { /* Handle favorite */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .border(
                            width = 1.dp,
                            color = colorScheme.outline.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colorScheme.surface.copy(alpha = 0.05f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FavoriteBorder,
                        contentDescription = "Save tip",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }

                // New Tip Button
                Button(
                    onClick = {
                        if (!isLoading) {
                            isLoading = true
                            rotationState += 360f
                            currentTipIndex++
                            viewModel.getNewTip()
                            // <<< CORRECT: Use scope.launch for the coroutine
                            scope.launch {
                                delay(800) // Slightly longer delay to see animation
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading, // <<< IMPROVEMENT: Disable button when loading
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary
                    )
                ) {
                    // <<< IMPROVEMENT: Show loading indicator
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "New tip",
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(rotation)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Tip")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom wellness indicators
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800, delayMillis = 600))
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WellnessIndicator(
                    icon = Icons.Rounded.Lightbulb,
                    label = "Insight",
                    colorScheme = colorScheme
                )
                WellnessIndicator(
                    icon = Icons.Rounded.Psychology,
                    label = "Mindful",
                    colorScheme = colorScheme
                )
                WellnessIndicator(
                    icon = Icons.Rounded.SelfImprovement,
                    label = "Calm",
                    colorScheme = colorScheme
                )
            }
        }
    }
}

@Composable
fun WellnessCategoryChip(
    category: WellnessCategory,
    colorScheme: ColorScheme
) {
    var isSelected by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                color = if (isSelected) category.color.copy(alpha = 0.1f)
                else colorScheme.surface.copy(alpha = 0.05f)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) category.color.copy(alpha = 0.3f)
                else colorScheme.outline.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { isSelected = !isSelected }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = if (isSelected) category.color else colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) category.color else colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun WellnessIndicator(
    icon: ImageVector,
    label: String,
    colorScheme: ColorScheme
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = colorScheme.primaryContainer.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
