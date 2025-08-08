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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.ui.viewmodel.BreathworkViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class BreathingTechnique(
    val name: String,
    val shortName: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val action: suspend BreathworkViewModel.() -> kotlinx.coroutines.flow.Flow<Pair<String, String>>
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    val infiniteTransition = rememberInfiniteTransition(label = "breathing_transition")
    val circleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive && (instruction.contains("In") || instruction.contains("Tense"))) 1.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when {
                    instruction.contains("In") -> 4000
                    instruction.contains("Hold") -> 4000 // Generic hold time
                    instruction.contains("Out") -> 4000
                    else -> 2000
                },
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle_scale"
    )

    val colorAnimation by animateColorAsState(
        targetValue = when {
            instruction.contains("In") -> Color(0xFF4CAF50).copy(alpha = 0.8f) // Green for Inhale
            instruction.contains("Hold") -> Color(0xFFFFC107).copy(alpha = 0.8f) // Amber for Hold
            instruction.contains("Out") || instruction.contains("Exhale") -> Color(0xFF2196F3).copy(alpha = 0.8f) // Blue for Exhale
            else -> selectedTechnique?.color?.copy(alpha = 0.5f) ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        },
        animationSpec = tween(1500, easing = EaseInOutCubic),
        label = "color_animation"
    )

    // Breathing techniques list
    val techniques = remember {
        listOf(
            BreathingTechnique("Box Breathing", "4-4-4-4", "Calm focus", Icons.Default.CropSquare, Color(0xFF6366F1)) { boxBreathing() },
            BreathingTechnique("Relaxing Breath", "4-7-8", "Aids sleep", Icons.Default.Bedtime, Color(0xFF8B5CF6)) { relaxingBreath478() },
            BreathingTechnique("Coherent", "5-5", "Balance", Icons.Default.Favorite, Color(0xFFEC4899)) { coherentBreathing() },
            BreathingTechnique("Wim Hof", "Power", "Energy & focus", Icons.Default.Whatshot, Color(0xFFEF4444)) { wimHofBreathing() },
            BreathingTechnique("Triangle", "3-3-3", "Simple rhythm", Icons.Default.ChangeHistory, Color(0xFF0EA5E9)) { triangleBreathing() },
            BreathingTechnique("Pursed Lip", "2-4", "Slows breath", Icons.Default.FilterVintage, Color(0xFF10B981)) { pursedLipBreathing() },
            BreathingTechnique("Belly Breathing", "4-6", "Deep relaxation", Icons.Default.SelfImprovement, Color(0xFF14B8A6)) { diaphragmaticBreathing() },
            BreathingTechnique("Alternate Nostril", "Nadi", "Balances mind", Icons.Default.SyncAlt, Color(0xFFF59E0B)) { alternateNostrilBreathing() },
            BreathingTechnique("Equal Breathing", "6-6", "Calming", Icons.Default.Balance, Color(0xFF65A30D)) { equalBreathing66() },
            BreathingTechnique("Humming Bee", "Bhramari", "Soothes anxiety", Icons.Default.GraphicEq, Color(0xFFD946EF)) { hummingBeeBreath() },
            BreathingTechnique("Ocean Breath", "Ujjayi", "Builds energy", Icons.Default.Waves, Color(0xFF0D9488)) { ujjayiBreath() },
            BreathingTechnique("Kapalabhati", "Cleanse", "Energizing", Icons.Default.Bolt, Color(0xFFF97316)) { kapalabhatiBreathing() },
            BreathingTechnique("Extended Exhale", "4-8", "Rest & digest", Icons.Default.TrendingDown, Color(0xFF22C55E)) { extendedExhale48() },
            BreathingTechnique("Buteyko Hold", "Reduce", "Improves O2", Icons.Default.PauseCircle, Color(0xFF78716C)) { buteykoHold() },
            BreathingTechnique("Cyclic Sighing", "Sigh", "Quickly de-stress", Icons.Default.DoubleArrow, Color(0xFF3B82F6)) { cyclicSighing() },
            BreathingTechnique("7-11 Breathing", "7-11", "For anxiety", Icons.Default.Thermostat, Color(0xFF84CC16)) { sevenElevenBreathing() },
            BreathingTechnique("Three-Part Breath", "Dirga", "Full lung use", Icons.Default.StackedLineChart, Color(0xFFF43F5E)) { threePartBreath() },
            BreathingTechnique("Cooling Breath", "Sitali", "Cools body", Icons.Default.AcUnit, Color(0xFF06B6D4)) { coolingBreathSitali() },
            BreathingTechnique("Lion's Breath", "Release", "Relieves tension", Icons.Default.SentimentVeryDissatisfied, Color(0xFFEAB308)) { lionsBreath() }
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
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (type) {
                "start" -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                "phase" -> VibrationEffect.createWaveform(longArrayOf(0, 30, 50, 30), -1)
                "stop" -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                else -> VibrationEffect.createOneShot(50, 50)
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            when (type) {
                "start" -> vibrator.vibrate(100)
                "phase" -> vibrator.vibrate(longArrayOf(0, 30, 50, 30), -1)
                "stop" -> vibrator.vibrate(200)
                else -> vibrator.vibrate(50)
            }
        }
    }

    // Track instruction changes for haptic feedback
    LaunchedEffect(instruction) {
        if (isActive && instruction.isNotEmpty() && instruction != "Get Ready...") {
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
                .verticalScroll(rememberScrollState()) // Make the column scrollable
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
                AnimatedVisibility(visible = isActive) {
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                        color = selectedTechnique?.color?.copy(alpha = 0.1f) ?: Color.Transparent
                    ) {
                        Text(
                            text = selectedTechnique?.shortName ?: "",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = selectedTechnique?.color ?: Color.Unspecified
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Breathing Circle Visualization
            Box(
                modifier = Modifier
                    .size(240.dp) // Reduced size to make more space
                    .scale(if (isActive) circleScale else 1f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2.5f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(colorAnimation.copy(alpha = 0.4f), Color.Transparent),
                            radius = radius * 1.5f
                        ),
                        radius = radius * 1.3f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(colors = listOf(colorAnimation, colorAnimation.copy(alpha = 0.2f))),
                        radius = radius
                    )
                    drawCircle(color = Color.White.copy(alpha = 0.1f), radius = radius * 0.7f)
                    if (isActive) {
                        drawCircle(color = Color.White.copy(alpha = 0.3f), radius = radius * 0.9f, style = Stroke(width = 3.dp.toPx()))
                    }
                }

                // Center content
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedContent(
                        targetState = instruction,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                        label = "instruction_animation"
                    ) { targetInstruction ->
                        Text(
                            text = targetInstruction,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, color = Color.White)
                        )
                    }
                    if (timer.all { it.isDigit() || it == 's' }) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AnimatedContent(
                            targetState = timer,
                            transitionSpec = { (slideInVertically { -it } + fadeIn()) togetherWith (slideOutVertically { it } + fadeOut()) },
                            label = "timer_animation"
                        ) { targetTimer ->
                            Text(
                                text = targetTimer,
                                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Light, color = Color.White.copy(alpha = 0.9f))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Technique Selection
            AnimatedVisibility(
                visible = !isActive,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Choose Your Technique",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        techniques.forEach { technique ->
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
            }

            // Spacer to push button to the bottom if content is short
            if (!isActive) {
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }


            // Control Button
            AnimatedContent(
                targetState = isActive,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop Session", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Stop Session", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                    }
                } else {
                    Text(
                        text = "Select a breathing technique to begin",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), textAlign = TextAlign.Center),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TechniqueCard(technique: BreathingTechnique, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1f, label = "card_scale")

    Surface(
        modifier = Modifier
            .width(150.dp) // Slightly wider cards for better text fit
            .height(180.dp) // Fixed height for uniform grid
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        shape = RoundedCornerShape(20.dp),
        // Use a subtle alpha for the background for a slick, flat look
        color = if (isSelected) technique.color.copy(alpha = 0.3f) else technique.color.copy(alpha = 0.05f),
        // Apply a border in both selected and unselected states
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) technique.color.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium, color = if (isSelected) technique.color else MaterialTheme.colorScheme.onSurface),
                textAlign = TextAlign.Center
            )
            Text(
                text = technique.shortName,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = technique.description,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 1
            )
        }
    }
}
