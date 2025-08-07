package com.hul0.mindflow.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.model.MoodEntry
import com.hul0.mindflow.ui.viewmodel.MoodTrackerViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodTrackerScreen(viewModel: MoodTrackerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    val moodHistory by viewModel.moodHistory.observeAsState(initial = emptyList())
    val haptic = LocalHapticFeedback.current

    // Enhanced mood data with colors and descriptions
    val moodData = listOf(
        MoodData("😄", "Excellent", Color(0xFF4CAF50)),
        MoodData("😊", "Good", Color(0xFF8BC34A)),
        MoodData("😐", "Okay", Color(0xFFFF9800)),
        MoodData("😔", "Not great", Color(0xFFFF5722)),
        MoodData("😠", "Terrible", Color(0xFFE91E63))
    )

    var selectedMood by remember { mutableStateOf("😊") }
    var justSelected by remember { mutableStateOf(false) }

    // Success celebration effect
    LaunchedEffect(justSelected) {
        if (justSelected) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(800)
            justSelected = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header section
        HeaderSection(
            justSelected = justSelected,
            modifier = Modifier.fillMaxWidth()
        )

        // Mood selection section
        MoodSelectionSection(
            moodData = moodData,
            selectedMood = selectedMood,
            onMoodSelected = { mood ->
                selectedMood = mood
                justSelected = true
                viewModel.addMoodEntry(MoodEntry(mood = mood, timestamp = System.currentTimeMillis()))
            },
            modifier = Modifier.fillMaxWidth()
        )

        // History section
        if (moodHistory.isNotEmpty()) {
            Text(
                "Your Journey",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(moodHistory) { index, entry ->
                    MoodHistoryRow(
                        entry = entry,
                        index = index,
                        moodData = moodData,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Start tracking your mood journey!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    justSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (justSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "header_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.scale(scale)
    ) {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Track your emotional wellness journey",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(
            visible = justSelected,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(2000)) + scaleOut(animationSpec = tween(2000))
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    "Thanks for sharing! 💚",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun MoodSelectionSection(
    moodData: List<MoodData>,
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // BUG FIX: Reduced padding to save horizontal space
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Responsive grid for mood buttons
            if (moodData.size <= 5) {
                // Single row for 5 or fewer items
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround, // BUG FIX: Changed arrangement for better fitting
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    moodData.forEach { mood ->
                        MoodButton(
                            moodData = mood,
                            isSelected = mood.emoji == selectedMood,
                            onClick = { onMoodSelected(mood.emoji) }
                        )
                    }
                }
            } else {
                // Grid layout for more items (future-proof)
                val chunkedMoods = moodData.chunked(3)
                chunkedMoods.forEach { rowMoods ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowMoods.forEach { mood ->
                            MoodButton(
                                moodData = mood,
                                isSelected = mood.emoji == selectedMood,
                                onClick = { onMoodSelected(mood.emoji) }
                            )
                        }
                        // Add spacers for incomplete rows
                        repeat(3 - rowMoods.size) {
                            Spacer(modifier = Modifier.width(52.dp))
                        }
                    }
                    if (rowMoods != chunkedMoods.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodButton(
    moodData: MoodData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "mood_button_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            moodData.color.copy(alpha = 0.15f)
        else
            Color.Transparent,
        animationSpec = tween(300),
        label = "mood_button_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected)
            moodData.color.copy(alpha = 0.5f)
        else
            Color.Transparent,
        animationSpec = tween(300),
        label = "mood_button_border"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp) // BUG FIX: Made the button smaller
                .scale(scale)
                .clip(CircleShape)
                .background(backgroundColor)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = borderColor,
                            shape = CircleShape
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = moodData.emoji,
                fontSize = 30.sp // BUG FIX: Made the emoji smaller
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = isSelected,
            enter = slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { it / 4 },
                animationSpec = tween(200)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            Text(
                text = moodData.description,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = moodData.color,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MoodHistoryRow(
    entry: MoodEntry,
    index: Int,
    moodData: List<MoodData>,
    modifier: Modifier = Modifier
) {
    val moodInfo = moodData.find { it.emoji == entry.mood }
        ?: MoodData(entry.mood, "Unknown", Color.Gray)

    // Staggered animation for list items
    val animationDelay = (index * 50).coerceAtMost(300)
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(300))
    ) {
        Surface(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mood circle with background
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(moodInfo.color.copy(alpha = 0.1f))
                        .border(
                            width = 1.dp,
                            color = moodInfo.color.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.mood,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = moodInfo.description,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val sdfDate = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
                    val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())

                    Text(
                        text = sdfDate.format(Date(entry.timestamp)),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 1
                    )

                    Text(
                        text = sdfTime.format(Date(entry.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                // Subtle indicator
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(moodInfo.color.copy(alpha = 0.6f))
                )
            }
        }
    }
}

private data class MoodData(
    val emoji: String,
    val description: String,
    val color: Color
)