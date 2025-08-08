package com.hul0.mindflow.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.ui.viewmodel.BmiCategory
import com.hul0.mindflow.ui.viewmodel.BmiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun BmiCalculatorScreen(viewModel: BmiViewModel = viewModel()) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Local state for UI effects that don't need to be in the ViewModel
    var isCalculating by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    var motivationIndex by remember { mutableStateOf(0) }

    // Determine color based on BMI category from the UI state
    val bmiColor = remember(uiState.bmiCategory) { categoryColor(uiState.bmiCategory) }

    // Dynamic background gradient based on BMI category
    val backgroundGradient = animateColorAsState(
        targetValue = when (uiState.bmiCategory) {
            BmiCategory.Underweight -> Color(0xFF1A365D)
            BmiCategory.Normal -> Color(0xFF22543D)
            BmiCategory.Overweight -> Color(0xFF744210)
            BmiCategory.Obese -> Color(0xFF742A2A)
            BmiCategory.None -> Color(0xFF2D3748)
        },
        animationSpec = tween(1000),
        label = "backgroundGradient"
    )

    // Trigger celebration animation when BMI is calculated
    LaunchedEffect(uiState.bmi) {
        if (uiState.bmi > 0f) {
            showCelebration = true
            delay(400) // Duration for the celebration effect
            showCelebration = false
        }
    }

    // Cycle through motivational messages
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            motivationIndex = (motivationIndex + 1) % motivationalMessages.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundGradient.value.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )
    ) {
        // Main content column, now scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Makes the column scrollable
                .systemBarsPadding()
                .imePadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DynamicHeader(showCelebration = showCelebration)

            MotivationalBanner(message = motivationalMessages[motivationIndex])

            EnhancedInputCard(
                height = uiState.height,
                weight = uiState.weight,
                onHeightChange = viewModel::onHeightChange,
                onWeightChange = viewModel::onWeightChange,
                isHeightValid = uiState.isHeightValid,
                isWeightValid = uiState.isWeightValid,
                bmiColor = bmiColor
            )

            val formValid = uiState.isHeightValid && uiState.isWeightValid && uiState.height.isNotBlank() && uiState.weight.isNotBlank()

            SuperCoolCalculateButton(
                enabled = formValid,
                isCalculating = isCalculating,
                onClick = {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        isCalculating = true
                        delay(500) // Simulate calculation delay for effect
                        viewModel.calculateBmi()
                        isCalculating = false
                    }
                },
                bmiColor = bmiColor
            )

            // Animated visibility for the results section
            AnimatedVisibility(
                visible = uiState.bmi > 0f,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(tween(600)),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EnhancedResultCard(
                        bmi = uiState.bmi,
                        category = uiState.bmiCategory,
                        color = bmiColor,
                        showCelebration = showCelebration
                    )

                    InteractiveInsightsRow(category = uiState.bmiCategory, color = bmiColor)

                    DetailedAnalysisCard(
                        bmi = uiState.bmi,
                        idealWeightRange = uiState.idealWeightRange,
                        category = uiState.bmiCategory,
                        color = bmiColor
                    )

                    ProgressJourneyCard(category = uiState.bmiCategory, color = bmiColor)
                }
            }

            Spacer(Modifier.weight(1f, fill = false)) // Use fill = false in scrollable columns

            EnhancedSupportText()
        }
    }
}

@Composable
private fun DynamicHeader(showCelebration: Boolean) {
    val celebrationScale by animateFloatAsState(
        targetValue = if (showCelebration) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "celebrationScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(celebrationScale),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667EEA).copy(alpha = 0.8f),
                            Color(0xFF764BA2).copy(alpha = 0.8f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.2f),
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "✨ MindFlow BMI ✨",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Your journey to a healthier you starts here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Health Icon",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
private fun MotivationalBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EnhancedInputCard(
    height: String,
    weight: String,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    isHeightValid: Boolean,
    isWeightValid: Boolean,
    bmiColor: Color
) {
    var heightFocused by remember { mutableStateOf(false) }
    var weightFocused by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            width = if (heightFocused || weightFocused) 2.dp else 1.dp,
            color = if (heightFocused || weightFocused) bmiColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Input,
                    contentDescription = null,
                    tint = bmiColor
                )
                Text(
                    text = "Your Body Metrics",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = bmiColor
                )
            }

            GradientTextField(
                value = height,
                onValueChange = onHeightChange,
                label = "Height",
                placeholder = "e.g. 172",
                leadingIcon = Icons.Outlined.Height,
                trailingText = "cm",
                isError = !isHeightValid,
                errorText = "Use a value between 80–250 cm",
                supportText = "in Centimeters",
                onFocusChanged = { heightFocused = it },
                bmiColor = bmiColor
            )

            GradientTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = "Weight",
                placeholder = "e.g. 68.5",
                leadingIcon = Icons.Outlined.Scale,
                trailingText = "kg",
                isError = !isWeightValid,
                errorText = "Use a value between 20–300 kg",
                supportText = "in Kilograms",
                onFocusChanged = { weightFocused = it },
                bmiColor = bmiColor
            )
        }
    }
}

@Composable
private fun GradientTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    trailingText: String,
    isError: Boolean,
    errorText: String,
    supportText: String,
    onFocusChanged: (Boolean) -> Unit,
    bmiColor: Color
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (isFocused) bmiColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        trailingIcon = {
            EnhancedUnitChip(text = trailingText, color = if (isFocused) bmiColor else MaterialTheme.colorScheme.primary)
        },
        isError = isError,
        supportingText = {
            Text(
                text = if (isError) errorText else supportText,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = bmiColor,
            focusedLabelColor = bmiColor,
            cursorColor = bmiColor,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChanged(focusState.isFocused)
            },
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}

@Composable
private fun EnhancedUnitChip(text: String, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SuperCoolCalculateButton(
    enabled: Boolean,
    isCalculating: Boolean,
    onClick: () -> Unit,
    bmiColor: Color
) {
    val buttonGradient = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(bmiColor.copy(alpha = 0.8f), bmiColor)
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )
    }

    Button(
        onClick = onClick,
        enabled = enabled && !isCalculating,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bmiColor,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        AnimatedContent(
            targetState = isCalculating,
            transitionSpec = { fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220)) },
            label = "calcButtonAnimation"
        ) { calculating ->
            if (calculating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Calculate,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "✨ Calculate BMI ✨",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedResultCard(
    bmi: Float,
    category: BmiCategory,
    color: Color,
    showCelebration: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = min(bmi / 40f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "progressAnimation"
    )

    val scale by animateFloatAsState(
        targetValue = if (showCelebration) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "celebrationScaleResult"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        border = BorderStroke(2.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 Your Result",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                EnhancedCategoryChip(category = category, color = color)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(color.copy(alpha = 0.2f), color.copy(alpha = 0.05f))
                            )
                        )
                        .border(3.dp, color.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "%.1f".format(bmi),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = color
                        )
                        Text(
                            text = "BMI",
                            style = MaterialTheme.typography.labelSmall,
                            color = color.copy(alpha = 0.7f)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedProgress)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(color.copy(alpha = 0.7f), color)
                                    )
                                )
                        )
                    }
                    Text(
                        text = "💡 Healthy range: 18.5–24.9",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedCategoryChip(category: BmiCategory, color: Color) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = category.label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

@Composable
private fun InteractiveInsightsRow(category: BmiCategory, color: Color) {
    // ... (rest of the composable remains the same)
    val insights = when (category) {
        BmiCategory.Underweight -> listOf(
            Triple("🍎 Boost calories", Icons.Filled.Restaurant, "Nutrient-dense foods"),
            Triple("💪 Protein focus", Icons.Filled.FitnessCenter, "Build muscle mass"),
            Triple("🏋️ Strength training", Icons.Filled.SportsGymnastics, "Resistance exercises")
        )
        BmiCategory.Normal -> listOf(
            Triple("🌟 Keep it up!", Icons.Filled.Star, "You're doing great"),
            Triple("🥗 Balanced diet", Icons.Filled.Restaurant, "Maintain variety"),
            Triple("🚀 Stay active", Icons.Filled.DirectionsRun, "Regular movement")
        )
        BmiCategory.Overweight -> listOf(
            Triple("📱 Calorie awareness", Icons.Filled.Insights, "Track your intake"),
            Triple("🥬 Fiber rich foods", Icons.Filled.Eco, "Feel fuller longer"),
            Triple("👟 Daily steps", Icons.Filled.DirectionsWalk, "8000+ steps")
        )
        BmiCategory.Obese -> listOf(
            Triple("👨‍⚕️ Guided plan", Icons.Filled.HealthAndSafety, "Professional help"),
            Triple("💊 Medical advice", Icons.Filled.LocalHospital, "Consult doctor"),
            Triple("🏊 Low-impact cardio", Icons.Filled.Pool, "Joint-friendly exercise")
        )
        BmiCategory.None -> emptyList()
    }

    if (insights.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(insights) { (text, icon, description) ->
            InteractiveInsightChip(
                text = text,
                icon = icon,
                description = description,
                color = color
            )
        }
    }
}

@Composable
private fun InteractiveInsightChip(
    text: String,
    icon: ImageVector,
    description: String,
    color: Color
) {
    // ... (rest of the composable remains the same)
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "insightChipScale"
    )

    Card(
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = color
            )
        }
    }
}


@Composable
private fun DetailedAnalysisCard(
    bmi: Float,
    idealWeightRange: Pair<Float, Float>,
    category: BmiCategory,
    color: Color
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .animateContentSize() // Animate size change when expanding/collapsing
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Analytics,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "📊 Detailed Analysis",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = color
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = color
                )
            }

            if (expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    HorizontalDivider(color = color.copy(alpha = 0.3f))
                    EnhancedKeyValueRow("BMI Score", "%.1f".format(bmi), color)
                    EnhancedKeyValueRow("Category", category.label, color)
                    EnhancedKeyValueRow("Healthy BMI Range", "18.5 – 24.9", color)
                    EnhancedKeyValueRow(
                        "Your Ideal Weight",
                        "%.1f – %.1f kg".format(max(0f, idealWeightRange.first), max(0f, idealWeightRange.second)),
                        color
                    )
                    Spacer(Modifier.height(8.dp))
                    PersonalizedGuidanceCard(category = category, color = color)
                    EnhancedActionRow(category = category, color = color)
                }
            }
        }
    }
}

// ... The rest of the helper composables (EnhancedKeyValueRow, PersonalizedGuidanceCard, etc.)
// and helper functions (categoryColor, guidanceText, motivationalMessages) remain largely the same.
// Make sure they are included in the final file.

@Composable
private fun EnhancedKeyValueRow(key: String, value: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = key,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun PersonalizedGuidanceCard(category: BmiCategory, color: Color) {
    val guidance = guidanceText(category)
    val emoji = when (category) {
        BmiCategory.Underweight -> "🌱"
        BmiCategory.Normal -> "🌟"
        BmiCategory.Overweight -> "⚖️"
        BmiCategory.Obese -> "🎯"
        BmiCategory.None -> "💭"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Personalized Guidance",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = color
                )
            }

            Text(
                text = guidance,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EnhancedActionRow(category: BmiCategory, color: Color) {
    val actions = when (category) {
        BmiCategory.Underweight -> listOf(
            Triple("🍽️ Meal Plan", Icons.Filled.RestaurantMenu, "Calorie-rich meals"),
            Triple("💪 Get Trainer", Icons.Filled.FitnessCenter, "Professional guidance")
        )
        BmiCategory.Normal -> listOf(
            Triple("📋 Maintain Plan", Icons.Filled.Assignment, "Keep current habits"),
            Triple("💧 Hydration", Icons.Filled.WaterDrop, "Stay well hydrated")
        )
        BmiCategory.Overweight -> listOf(
            Triple("📱 Calorie Tracker", Icons.Filled.TrendingDown, "Monitor intake"),
            Triple("🚶 Walk 8K+", Icons.Filled.DirectionsWalk, "Daily step goal")
        )
        BmiCategory.Obese -> listOf(
            Triple("🏥 Care Team", Icons.Filled.Groups, "Medical support"),
            Triple("🥗 Nutritionist", Icons.Filled.Restaurant, "Diet planning")
        )
        BmiCategory.None -> emptyList()
    }

    if (actions.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(actions) { (label, icon, description) ->
            EnhancedActionButton(
                label = label,
                icon = icon,
                color = color
            )
        }
    }
}

@Composable
private fun EnhancedActionButton(
    label: String,
    icon: ImageVector,
    color: Color
) {
    OutlinedButton(
        onClick = { /* Add navigation logic or show info */ },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = color
            )
        }
    }
}

@Composable
private fun ProgressJourneyCard(category: BmiCategory, color: Color) {
    // ... (This composable can remain as is)
    val journeySteps = listOf(
        "📝 Assessment",
        "🎯 Goal Setting",
        "📊 Tracking",
        "🏆 Achievement"
    )

    val currentStep = when (category) {
        BmiCategory.None -> 0
        else -> 1
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Timeline,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = "🚀 Your Health Journey",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = color
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                journeySteps.forEachIndexed { index, step ->
                    JourneyStepIndicator(
                        step = step,
                        isActive = index <= currentStep,
                        isCompleted = index < currentStep,
                        color = color,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun JourneyStepIndicator(
    step: String,
    isActive: Boolean,
    isCompleted: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    // ... (This composable can remain as is)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> color
                        isActive -> color.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isActive || isCompleted) color.copy(alpha = 0.5f) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Completed",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = step,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive || isCompleted) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun EnhancedSupportText() {
    // ... (This composable can remain as is)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.HealthAndSafety,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = "⚕️ Medical Disclaimer",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "BMI is a screening tool, not a diagnostic one. Always consult a healthcare provider for personalized advice and a comprehensive health assessment.",
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.3
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}


// Helper functions
private fun categoryColor(cat: BmiCategory): Color {
    return when (cat) {
        BmiCategory.Underweight -> Color(0xFF3B82F6) // vibrant blue
        BmiCategory.Normal -> Color(0xFF10B981) // emerald green
        BmiCategory.Overweight -> Color(0xFFF59E0B) // amber
        BmiCategory.Obese -> Color(0xFFEF4444) // red
        BmiCategory.None -> Color(0xFF8B5CF6) // purple
    }
}

private fun guidanceText(category: BmiCategory): String {
    return when (category) {
        BmiCategory.Underweight ->
            "Your BMI suggests you're in the underweight range. Focus on nutrient-dense, calorie-rich foods and consider strength training to build healthy muscle mass."
        BmiCategory.Normal ->
            "Excellent! You're in the healthy weight range. Maintain your current lifestyle with balanced nutrition and regular physical activity."
        BmiCategory.Overweight ->
            "You're slightly above the healthy weight range. Small changes like portion control and daily walks can make a big difference."
        BmiCategory.Obese ->
            "Your BMI is above the recommended range. It's a good idea to consult with a healthcare professional to develop a comprehensive, sustainable plan."
        BmiCategory.None -> "Complete your assessment to receive personalized guidance and start your journey toward optimal health."
    }
}

private val motivationalMessages = listOf(
    "Your health journey starts with a single step! 🚀",
    "Every measurement is progress toward a healthier you! ✨",
    "Small changes lead to big transformations! 💪",
    "You're investing in your future self today! 🌟",
    "Health is the greatest wealth you can build! �",
    "Progress over perfection, always! 🎯",
    "Your body is your temple – treat it with love! ❤️",
    "Consistency beats perfection every time! 🏆"
)
