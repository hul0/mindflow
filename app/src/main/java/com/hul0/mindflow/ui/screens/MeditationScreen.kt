package com.hul0.mindflow.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Data classes for our models
data class MeditationSession(
    val name: String,
    val duration: Int, // in minutes
    val icon: ImageVector,
    val description: String,
    val color: Color
)

data class AmbientSound(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val isPlaying: Boolean = false
)
object VibrantColors {

    val Blue = Color(0xFF3A86FF)

    val Purple = Color(0xFF8338EC)

    val Pink = Color(0xFFFF006E)

    val Orange = Color(0xFFFB5607)

    val Yellow = Color(0xFFFFBE0B)

    val Teal = Color(0xFF2EC4B6)

    val Green = Color(0xFF4CAF50)

    val Red = Color(0xFFF44336)

//Color(0xFF0B0215) // Dark purple background

    val OnBackground = Color(0xFFF0E7FE)

    val OnBackgroundSecondary = Color(0xFFF0E7FE).copy(alpha = 0.7f)

}


@Composable
fun MeditationScreen() {
    // State management for the screen
    var selectedSession by remember { mutableStateOf<MeditationSession?>(null) }
    var isSessionActive by remember { mutableStateOf(false) }
    var sessionTimeRemaining by remember { mutableStateOf(0) }
    var breathingPhase by remember { mutableStateOf("Inhale") }

    val sessions = remember {
        listOf(
            MeditationSession("Quick Focus", 5, Icons.Default.Timer, "Short mindfulness break", VibrantColors.Blue),
            MeditationSession("Deep Calm", 15, Icons.Default.SelfImprovement, "Extended relaxation session", VibrantColors.Purple),
            MeditationSession("Sleep Prep", 10, Icons.Default.Bedtime, "Prepare your mind for rest", VibrantColors.Teal),
            MeditationSession("Anxiety Relief", 12, Icons.Default.Healing, "Calm racing thoughts", VibrantColors.Pink),
            MeditationSession("Body Scan", 20, Icons.Default.Accessibility, "Progressive muscle relaxation", VibrantColors.Orange)
        )
    }

    // List of ambient sounds with assigned colors
    var ambientSounds by remember {
        mutableStateOf(listOf(
            AmbientSound("Rain", Icons.Default.Cloud, VibrantColors.Blue),
            AmbientSound("Ocean", Icons.Default.Waves, VibrantColors.Teal),
            AmbientSound("Forest", Icons.Default.Park, VibrantColors.Green),
            AmbientSound("Fire", Icons.Default.Fireplace, VibrantColors.Orange),
            AmbientSound("Wind", Icons.Default.Air, VibrantColors.Purple)
        ))
    }

    // Breathing animation scale
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    // Timer and breathing phase logic
    LaunchedEffect(isSessionActive, sessionTimeRemaining) {
        if (isSessionActive && sessionTimeRemaining > 0) {
            delay(1000)
            sessionTimeRemaining--
            // Update breathing cycle every 8 seconds (4s inhale, 4s exhale)
            val cycleTime = (selectedSession!!.duration * 60 - sessionTimeRemaining) % 8
            breathingPhase = if (cycleTime < 4) "Inhale" else "Exhale"
        } else if (sessionTimeRemaining <= 0 && isSessionActive) {
            isSessionActive = false
            selectedSession = null
        }
    }

    // Main layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            ScreenHeader()
            Spacer(modifier = Modifier.height(24.dp))

            // Content switcher: Active session or session selection
            if (isSessionActive && selectedSession != null) {
                ActiveSessionContent(
                    session = selectedSession!!,
                    timeRemaining = sessionTimeRemaining,
                    breathingPhase = breathingPhase,
                    breathingScale = breathingScale,
                    onStop = {
                        isSessionActive = false
                        selectedSession = null
                        sessionTimeRemaining = 0
                    }
                )
            } else {
                SessionSelectionContent(
                    sessions = sessions,
                    ambientSounds = ambientSounds,
                    onSessionSelect = { session ->
                        selectedSession = session
                        sessionTimeRemaining = session.duration * 60
                        isSessionActive = true
                    },
                    onSoundToggle = { toggledSound ->
                        ambientSounds = ambientSounds.map {
                            if (it.name == toggledSound.name) it.copy(isPlaying = !it.isPlaying)
                            else it
                        }
                    }
                )
            }
            Spacer(Modifier.size(100.dp))
        }
    }
}

@Composable
private fun ScreenHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "MindFlow",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "Find your inner peace",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = { /* TODO: Settings action */ },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun ActiveSessionContent(
    session: MeditationSession,
    timeRemaining: Int,
    breathingPhase: String,
    breathingScale: Float,
    onStop: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = session.name,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        // Breathing Visualizer
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(300.dp)
        ) {
            val animatedColor by animateColorAsState(
                targetValue = if (breathingPhase == "Inhale") session.color else session.color.copy(alpha = 0.5f),
                animationSpec = tween(4000), label = "color_anim"
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(breathingScale)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2.5f

                // Outer decorative circles
                drawCircle(
                    color = session.color.copy(alpha = 0.1f),
                    radius = radius * 1.2f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
                drawCircle(
                    color = session.color.copy(alpha = 0.05f),
                    radius = radius * 1.4f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Main breathing circle with gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(animatedColor.copy(alpha = 0.8f), Color.Transparent),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
                drawCircle(
                    color = animatedColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Text and Icon inside the circle
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    session.icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = breathingPhase,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light),
                    color = Color.White
                )
            }
        }

        Text(
            text = "${timeRemaining / 60}:${String.format("%02d", timeRemaining % 60)}",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Light),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Stop Button
        Button(
            onClick = onStop,
            shape = CircleShape,
            modifier = Modifier.size(72.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.error
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun SessionSelectionContent(
    sessions: List<MeditationSession>,
    ambientSounds: List<AmbientSound>,
    onSessionSelect: (MeditationSession) -> Unit,
    onSoundToggle: (AmbientSound) -> Unit
) {
    Column {
        Text(
            text = "Choose Your Session",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Session List
        sessions.forEach { session ->
            SessionCard(session = session, onClick = { onSessionSelect(session) })
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ambient Sounds Section
        Text(
            text = "Ambient Sounds",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(ambientSounds) { _, sound ->
                AmbientSoundCard(sound = sound, onClick = { onSoundToggle(sound) })
            }
        }
    }
}

@Composable
private fun SessionCard(session: MeditationSession, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, session.color.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = session.color.copy(alpha = 0.07f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(session.color.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, session.color.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    session.icon,
                    contentDescription = null,
                    tint = session.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = session.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${session.duration} min",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                color = session.color
            )
        }
    }
}

@Composable
private fun AmbientSoundCard(sound: AmbientSound, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (sound.isPlaying) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "soundScale"
    )
    val color by animateColorAsState(
        targetValue = if (sound.isPlaying) sound.color else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300),
        label = "soundColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (sound.isPlaying) sound.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "soundBorderColor"
    )
    val iconColor by animateColorAsState(
        targetValue = if (sound.isPlaying) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "soundIconColor"
    )

    Card(
        modifier = Modifier
            .size(90.dp)
            .scale(scale)
            .clickable {
                onClick()
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            .border(2.dp, borderColor, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                sound.icon,
                contentDescription = sound.name,
                modifier = Modifier.size(36.dp),
                tint = iconColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sound.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
