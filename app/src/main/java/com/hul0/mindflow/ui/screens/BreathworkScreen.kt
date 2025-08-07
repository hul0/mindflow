// app/src/main/java/com/hul0/mindflow/ui/screens/BreathworkScreen.kt
package com.hul0.mindflow.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.ui.viewmodel.BreathworkViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.*

data class BreathingTechnique(
    val name: String,
    val shortName: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val action: suspend BreathworkViewModel.() -> kotlinx.coroutines.flow.Flow<Pair<String, String>>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathworkScreen(viewModel: BreathworkViewModel = viewModel()) {
    val instruction by viewModel.instruction.collectAsState()
    val timer by viewModel.timer.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    var selectedTechnique by remember { mutableStateOf<BreathingTechnique?>(null) }
    var isActive by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Animation states
    val breathingAnimation = rememberInfiniteTransition(label = "breathing")
    val circleScale by breathingAnimation.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive && (instruction.contains("In") || instruction.contains("Hold"))) 1.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when {
                    instruction.contains("In") -> 4000
                    instruction.contains("Hold") -> if (selectedTechnique?.shortName == "4-5-4") 5000 else 4000
                    instruction.contains("Out") -> 4000
                    else -> 1000
                },
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle_scale"
    )

    val colorAnimation by breathingAnimation.animateColor(
        initialValue = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        targetValue = when {
            instruction.contains("In") -> Color(0xFF4CAF50).copy(alpha = 0.8f)
            instruction.contains("Hold") -> Color(0xFFFF9800).copy(alpha = 0.8f)
            instruction.contains("Out") -> Color(0xFF2196F3).copy(alpha = 0.8f)
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        },
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color_animation"
    )

    // Breathing techniques
    val techniques = remember {
        listOf(
            BreathingTechnique(
                name = "Box Breathing",
                shortName = "4-4-4-4",
                description = "Equal breathing for calm focus",
                icon = Icons.Default.CropSquare,
                color = Color(0xFF6366F1)
            ) { boxBreathing() },
            BreathingTechnique(
                name = "Calm Breathing",
                shortName = "4-5-4",
                description = "Extended hold for relaxation",
                icon = Icons.Default.Spa,
                color = Color(0xFF10B981)
            ) { fourFiveFourBreathing() },
            BreathingTechnique(
                name = "Power Breathing",
                shortName = "4-7-8",
                description = "Deep relaxation technique",
                icon = Icons.Default.FlashOn,
                color = Color(0xFFF59E0B)
            ) { powerBreathing() },
            BreathingTechnique(
                name = "Wim Hof",
                shortName = "30-15",
                description = "Energizing breath method",
                icon = Icons.Default.Whatshot,
                color = Color(0xFFEF4444)
            ) { wimHofBreathing() },
            BreathingTechnique(
                name = "Triangle",
                shortName = "4-4-4",
                description = "Simple rhythmic breathing",
                icon = Icons.Default.ChangeHistory,
                color = Color(0xFF8B5CF6)
            ) { triangleBreathing() },
            BreathingTechnique(
                name = "Coherent",
                shortName = "5-5",
                description = "Heart rhythm coherence",
                icon = Icons.Default.Favorite,
                color = Color(0xFFEC4899)
            ) { coherentBreathing() }
        )
    }

    // Haptic feedback function
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun triggerHaptic(type: String) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (type) {
                "start" -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                "phase" -> VibrationEffect.createOneShot(50, 80)
                "stop" -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                else -> VibrationEffect.createOneShot(50, 50)
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            when (type) {
                "start" -> vibrator.vibrate(100)
                "phase" -> vibrator.vibrate(50)
                "stop" -> vibrator.vibrate(200)
                else -> vibrator.vibrate(50)
            }
        }
    }

    // Track instruction changes for haptic feedback
    LaunchedEffect(instruction) {
        if (isActive && instruction.isNotEmpty()) {
            triggerHaptic("phase")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Breathwork",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                if (isActive) {
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                        color = selectedTechnique?.color?.copy(alpha = 0.1f) ?: Color.Transparent
                    ) {
                        Text(
                            text = selectedTechnique?.shortName ?: "",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = selectedTechnique?.color ?: Color(0, 0, 0, 255)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Breathing Circle Visualization
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .scale(circleScale),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val center = size.center
                    val radius = minOf(size.width, size.height) / 2.5f

                    // Outer glow effect
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colorAnimation.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            radius = radius * 1.5f
                        ),
                        radius = radius * 1.3f,
                        center = center
                    )

                    // Main circle
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colorAnimation,
                                colorAnimation.copy(alpha = 0.2f)
                            )
                        ),
                        radius = radius,
                        center = center
                    )

                    // Inner circle
                    drawCircle(
                        color = Color.White.copy(alpha = 0.1f),
                        radius = radius * 0.7f,
                        center = center
                    )

                    // Progress ring
                    if (isActive) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.3f),
                            radius = radius * 0.9f,
                            center = center,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }

                // Center content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedContent(
                        targetState = instruction,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                        },
                        label = "instruction_animation"
                    ) { targetInstruction ->
                        Text(
                            text = targetInstruction,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        )
                    }

                    if (timer != "Ready?" && timer != "Start") {
                        Spacer(modifier = Modifier.height(8.dp))
                        AnimatedContent(
                            targetState = timer,
                            transitionSpec = {
                                (slideInVertically { -it } + fadeIn()) togetherWith
                                        (slideOutVertically { it } + fadeOut())
                            },
                            label = "timer_animation"
                        ) { targetTimer ->
                            Text(
                                text = targetTimer,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Light,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Technique Selection
            if (!isActive) {
                Text(
                    text = "Choose Your Technique",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(techniques) { technique ->
                        TechniqueCard(
                            technique = technique,
                            isSelected = selectedTechnique == technique,
                            onClick = {
                                selectedTechnique = technique
                                job?.cancel()
                                isActive = true
                                triggerHaptic("start")
                                job = coroutineScope.launch {
                                    technique.action(viewModel).collect()
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Control Button
            AnimatedContent(
                targetState = isActive,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "button_animation"
            ) { active ->
                if (active) {
                    Button(
                        onClick = {
                            job?.cancel()
                            isActive = false
                            selectedTechnique = null
                            triggerHaptic("stop")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stop Session",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Text(
                            text = "Select a breathing technique to begin",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TechniqueCard(
    technique: BreathingTechnique,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = Modifier
            .width(140.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) {
            technique.color.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        },
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, technique.color.copy(alpha = 0.5f))
        } else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = technique.color.copy(alpha = if (isSelected) 0.2f else 0.1f)
            ) {
                Icon(
                    imageVector = technique.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(),
                    tint = technique.color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = technique.name,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) technique.color else MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = technique.shortName,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = technique.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}