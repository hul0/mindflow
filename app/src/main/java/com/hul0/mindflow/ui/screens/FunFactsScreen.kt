// app/src/main/java/com/hul0/mindflow/ui/screens/FunFactsScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.hul0.mindflow.ui.viewmodel.FunFactsViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunFactsScreen(
    viewModel: FunFactsViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val funFact by viewModel.funFact.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    var factCount by remember { mutableStateOf(1) }
    var selectedCategory by remember { mutableStateOf("random") }

    // Animation states
    val cardScale by animateFloatAsState(
        targetValue = if (isLoading) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val buttonRotation by animateFloatAsState(
        targetValue = if (isLoading) 360f else 0f,
        animationSpec = tween(1000, easing = LinearEasing)
    )

    // Celebration particles
    LaunchedEffect(showCelebration) {
        if (showCelebration) {
            delay(1500)
            showCelebration = false
        }
    }

    // Fun fact categories with icons
    val categories = listOf(
        "random" to Icons.Filled.Shuffle,
        "science" to Icons.Outlined.Science,
        "animals" to Icons.Outlined.Pets,
        "space" to Icons.Outlined.RocketLaunch,
        "history" to Icons.Outlined.HistoryEdu,
        "food" to Icons.Outlined.Restaurant,
        "nature" to Icons.Outlined.Nature,
        "tech" to Icons.Outlined.Computer
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                    Column {
                        Text(
                            text = "Fun Facts",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Discovered: $factCount",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { /* Share functionality */ },
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { /* Bookmark functionality */ },
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Outlined.BookmarkAdd,
                            contentDescription = "Bookmark",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        // Category selector
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(categories) { (category, icon) ->
                val isSelected = selectedCategory == category
                FilterChip(
                    onClick = { selectedCategory = category },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                category.replaceFirstChar { it.uppercase() },
                                fontSize = 13.sp
                            )
                        }
                    },
                    selected = isSelected,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    ),
                    border = if (isSelected) {
                        FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = true,
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    } else null
                )
            }
        }

        // Main fun fact card
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(cardScale)
                    .clickable(enabled = !isLoading) {
                        if (!isLoading) {
                            isLoading = true
                            showCelebration = true
                            factCount++
                            viewModel.getNewFunFact()
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    AnimatedContent(
                        targetState = funFact?.fact ?: "Loading fun fact...",
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(600)) + slideInVertically { it / 2 })
                                .togetherWith(
                                    fadeOut(animationSpec = tween(300)) + slideOutVertically { -it / 2 }
                                )
                        },
                        modifier = Modifier.align(Alignment.Center)
                    ) { fact ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )

                            Text(
                                text = fact,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Loading indicator
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Celebration particles

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(5) { index ->
                        val delay = index * 100
                        var animate by remember { mutableStateOf(false) }

                        LaunchedEffect(showCelebration) {
                            if (showCelebration) {
                                delay(delay.toLong())
                                animate = true
                            } else {
                                animate = false
                            }
                        }

                        val offsetY by animateFloatAsState(
                            targetValue = if (animate) -100f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )

                        Icon(
                            listOf(Icons.Filled.Star, Icons.Filled.Favorite, Icons.Filled.ThumbUp).random(),
                            contentDescription = null,
                            modifier = Modifier
                                .offset(y = offsetY.dp)
                                .size(24.dp),
                            tint = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFF69B4),
                                Color(0xFF00CED1),
                                Color(0xFFFF6347),
                                Color(0xFF32CD32)
                            ).random().copy(alpha = 0.8f)
                        )
                    }
                }
            }


        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main action button
            Button(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        showCelebration = true
                        factCount++
                        viewModel.getNewFunFact()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(buttonRotation)
                    )
                    Text(
                        "Another one!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick actions
            IconButton(
                onClick = { /* Quick random fact */ },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    Icons.Filled.Casino,
                    contentDescription = "Random",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            IconButton(
                onClick = { /* Surprise me */ },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    Icons.Filled.Celebration,
                    contentDescription = "Surprise",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Bottom stats bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Outlined.Visibility,
                    label = "Views",
                    value = factCount.toString()
                )
                StatItem(
                    icon = Icons.Outlined.Psychology,
                    label = "Mind Blown",
                    value = "${factCount * 47}%"
                )
                StatItem(
                    icon = Icons.Outlined.EmojiEvents,
                    label = "Level",
                    value = "${factCount / 5 + 1}"
                )
            }
        }
    }

    // Reset loading state when fact changes
    LaunchedEffect(funFact) {
        if (funFact != null) {
            delay(500) // Small delay for smooth animation
            isLoading = false
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}