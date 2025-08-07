// app/src/main/java/com/hul0/mindflow/ui/screens/SleepTrackerScreen.kt
package com.hul0.mindflow.ui.screens

import android.app.TimePickerDialog
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.model.SleepSession
import com.hul0.mindflow.ui.viewmodel.SleepTrackerViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sin

// --- Data Structures for Robust Sleep Analysis ---

/**
 * A detailed breakdown of the sleep analysis. This is more robust
 * than a simple Pair, allowing for richer data presentation.
 */
data class SleepAnalysisResult(
    val totalDurationHours: Int,
    val totalDurationMinutes: Int,
    val quality: SleepQuality,
    val score: Int, // A comprehensive score out of 100
    val sleepCycles: Int,
    val feedback: String // Actionable feedback for the user
)

/**
 * Enum to represent sleep quality categories in a type-safe way.
 */
enum class SleepQuality(val description: String, val color: Color) {
    EXCELLENT("Excellent", Color(0xFF4CAF50)),
    GOOD("Good", Color(0xFF8BC34A)),
    FAIR("Fair", Color(0xFFFF9800)),
    POOR("Poor", Color(0xFFF44336))
}

// --- Main Analysis Function ---

/**
 * Analyzes a sleep session to determine its quality based on duration,
 * timing, and sleep cycles.
 *
 * @param session The SleepSession object containing start and end times.
 * @return A SleepAnalysisResult with a detailed breakdown.
 */
fun analyzeSleepSession(session: SleepSession): SleepAnalysisResult {
    val MILLIS_PER_MINUTE = 60_000L
    val IDEAL_SLEEP_CYCLE_MINS = 90
    val MAX_SCORE = 100.0

    val durationMillis = (session.wakeUpTime - session.bedTime).coerceAtLeast(0)
    val totalMinutes = (durationMillis / MILLIS_PER_MINUTE).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    val durationScore = when (totalMinutes) {
        in 450..510 -> 50.0 // 7.5h to 8.5h: Perfect
        in 420..540 -> 45.0 // 7h to 9h: Great
        in 360..600 -> 35.0 // 6h to 10h: Okay
        in 300..660 -> 20.0 // 5h to 11h: Sub-optimal
        in 0 .. 300 -> 5.0
        else -> 10.0
    }

    val sleepCycles = (totalMinutes.toDouble() / IDEAL_SLEEP_CYCLE_MINS).roundToInt()
    val cycleRemainder = totalMinutes % IDEAL_SLEEP_CYCLE_MINS
    val cycleScore = if (cycleRemainder < 15 || cycleRemainder > 75) 30.0 else 15.0

    val calendar = Calendar.getInstance().apply { timeInMillis = session.bedTime }
    val bedtimeHour = calendar.get(Calendar.HOUR_OF_DAY)
    val bedtimeScore = when (bedtimeHour) {
        22, 23 -> 20.0 // 10 PM - 11:59 PM: Optimal
        21, 0 -> 15.0  // 9 PM or 12 AM: Good
        else -> 5.0
    }

    val totalScore = (durationScore + cycleScore + bedtimeScore).roundToInt().coerceIn(0, 100)

    val quality = when {
        totalScore >= 85 -> SleepQuality.EXCELLENT
        totalScore >= 70 -> SleepQuality.GOOD
        totalScore >= 50 -> SleepQuality.FAIR
        else -> SleepQuality.POOR
    }

    val feedback = when (quality) {
        SleepQuality.EXCELLENT -> "Perfectly balanced sleep. Keep it up!"
        SleepQuality.GOOD -> "Great job! Your sleep is consistent."
        SleepQuality.FAIR -> "Good effort. Try going to bed a bit earlier."
        SleepQuality.POOR -> "Aim for 7-9 hours of consistent rest."
    }

    return SleepAnalysisResult(
        totalDurationHours = hours,
        totalDurationMinutes = minutes,
        quality = quality,
        score = totalScore,
        sleepCycles = sleepCycles.coerceAtLeast(0),
        feedback = feedback
    )
}


@Composable
fun SleepTrackerScreen(viewModel: SleepTrackerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    val context = LocalContext.current
    var bedTime by remember { mutableStateOf<Calendar?>(null) }
    var wakeUpTime by remember { mutableStateOf<Calendar?>(null) }
    val sleepSessions by viewModel.allSleepSessions.observeAsState(initial = emptyList())

    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val deepNight = Color(0xFF1A1A2E)
    val sleepyPurple = Color(0xFF16213E)
    val dreamBlue = Color(0xFF0F3460)
    val moonWhite = Color(0xFFE94560)
    val softGlow = Color(0xFF533483)

    val infiniteTransition = rememberInfiniteTransition(label = "sleep_screen_transition")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_anim"
    )

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val stars = remember { generateStars(50, screenWidthPx, screenHeightPx) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(deepNight, sleepyPurple, dreamBlue.copy(alpha = 0.8f))
                )
            )
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.6f)
        ) {
            drawStars(stars, floatAnim)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .offset(y = floatAnim.dp),
                        tint = moonWhite.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sleep Sanctuary",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Track your journey to dreamland",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Light
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimeSelectionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.NightlightRound,
                        title = "Bedtime",
                        time = bedTime?.let { timeFormatter.format(it.time) },
                        color = softGlow,
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(context, { _, hour, minute ->
                                cal.set(Calendar.HOUR_OF_DAY, hour)
                                cal.set(Calendar.MINUTE, minute)
                                bedTime = cal
                            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
                        }
                    )
                    TimeSelectionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.WbSunny,
                        title = "Wake Up",
                        time = wakeUpTime?.let { timeFormatter.format(it.time) },
                        color = moonWhite,
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(context, { _, hour, minute ->
                                cal.set(Calendar.HOUR_OF_DAY, hour)
                                cal.set(Calendar.MINUTE, minute)
                                wakeUpTime = cal
                            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
                        }
                    )
                }
            }

            item {
                val isEnabled = bedTime != null && wakeUpTime != null
                Button(
                    onClick = {
                        if (bedTime != null && wakeUpTime != null) {
                            viewModel.insert(SleepSession(bedTime = bedTime!!.timeInMillis, wakeUpTime = wakeUpTime!!.timeInMillis))
                            bedTime = null
                            wakeUpTime = null
                        }
                    },
                    enabled = isEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = moonWhite,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Save Sleep Session", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            if (sleepSessions.isNotEmpty()) {
                item {
                    Text(
                        text = "Sleep History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(sleepSessions.sortedByDescending { it.bedTime }) { session ->
                    SleepSessionCard(session = session, timeFormatter = timeFormatter)
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun TimeSelectionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    time: String?,
    color: Color,
    onClick: () -> Unit
) {
    val isSelected = time != null
    val cardColor by animateColorAsState(
        targetValue = if (isSelected) color.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
        animationSpec = tween(300),
        label = "card_color_anim"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) color else Color.White.copy(alpha = 0.3f),
        animationSpec = tween(300),
        label = "border_color_anim"
    )

    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() }
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) color else Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = time ?: "Select",
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SleepSessionCard(session: SleepSession, timeFormatter: SimpleDateFormat) {
    val analysis = analyzeSleepSession(session)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(analysis.quality.color.copy(alpha = 0.2f))
                        .border(2.dp, analysis.quality.color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${analysis.totalDurationHours}h",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${analysis.totalDurationMinutes}m",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = analysis.quality.description,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = analysis.quality.color
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Score: ${analysis.score}/100",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Cycles: ~${analysis.sleepCycles}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"${analysis.feedback}\"",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class Star(val x: Float, val y: Float, val alpha: Float, val size: Float)

private val random = Random(123)

fun generateStars(count: Int, width: Float, height: Float): List<Star> {
    return (1..count).map {
        Star(
            x = random.nextFloat() * width,
            y = random.nextFloat() * height,
            alpha = random.nextFloat().coerceIn(0.3f, 0.8f),
            size = (random.nextFloat() * 2f) + 1f
        )
    }
}

fun DrawScope.drawStars(stars: List<Star>, animationOffset: Float) {
    stars.forEach { star ->
        val twinkleAlpha = star.alpha + sin(animationOffset * 0.1f + star.x * 0.01f) * 0.2f
        drawCircle(
            color = Color.White.copy(alpha = twinkleAlpha.coerceIn(0f, 1f)),
            radius = star.size,
            center = Offset(star.x, star.y)
        )
    }
}
