package com.hul0.mindflow.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.hul0.mindflow.model.UserProfile
import com.hul0.mindflow.ui.viewmodel.ProfileViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.reflect.KMutableProperty1

// --- COLOR PALETTE ---
object ProfileColors {
    val Personal = Color(0xFF6C63FF)
    val Health = Color(0xFFFF6B6B)
    val Lifestyle = Color(0xFF4ECDC4)
    val Preferences = Color(0xFFFFD166)
    val Goals = Color(0xFF72DDF7)
}

// --- DATA & ENUMS ---

enum class ProfileCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    PERSONAL("Personal", Icons.Default.Person, ProfileColors.Personal),
    HEALTH("Health", Icons.Default.FavoriteBorder, ProfileColors.Health),
    LIFESTYLE("Lifestyle", Icons.AutoMirrored.Filled.DirectionsRun, ProfileColors.Lifestyle),
    PREFERENCES("Preferences", Icons.Default.StarBorder, ProfileColors.Preferences),
    GOALS("Goals & Growth", Icons.AutoMirrored.Filled.TrendingUp, ProfileColors.Goals)
}

/**
 * A sealed class to represent different types of input fields in the form.
 * This allows for type-safe handling of various UI controls.
 */
sealed class FormField {
    abstract val label: String
    abstract val icon: ImageVector?

    data class Text(
        override val label: String,
        override val icon: ImageVector?,
        val property: KMutableProperty1<UserProfile, String>,
        val singleLine: Boolean = true
    ) : FormField()

    data class Dropdown(
        override val label: String,
        override val icon: ImageVector?,
        val property: KMutableProperty1<UserProfile, String>,
        val options: List<String>
    ) : FormField()

    data class Slider(
        override val label: String,
        override val icon: ImageVector?,
        val property: KMutableProperty1<UserProfile, Float>,
        val range: ClosedFloatingPointRange<Float>,
        val steps: Int,
        val unit: String
    ) : FormField()

    data class Date(
        override val label: String,
        override val icon: ImageVector?,
        val property: KMutableProperty1<UserProfile, String>
    ) : FormField()

    data class Radio(
        override val label: String,
        override val icon: ImageVector?,
        val property: KMutableProperty1<UserProfile, String>,
        val options: List<String>
    ) : FormField()
}

// --- MAIN SCREEN COMPOSABLE ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory(application)
    )

    val profile by profileViewModel.profileState.collectAsState()
    var selectedCategory by remember { mutableStateOf(ProfileCategory.PERSONAL) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            profileViewModel.exportProfile(context, it)
            Toast.makeText(context, "Profile exported successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            profileViewModel.importProfile(context, it)
            Toast.makeText(context, "Profile imported successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Profile", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.padding(start = 1.dp)
                    )
                },
                actions = {
                    // Ravishing Import Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF667eea).copy(alpha = 0.1f),
                                        Color(0xFF764ba2).copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF667eea).copy(alpha = 0.4f),
                                        Color(0xFF764ba2).copy(alpha = 0.3f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { openDocumentLauncher.launch(arrayOf("text/csv", "text/comma-separated-values")) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                Icons.Default.FileUpload,
                                contentDescription = "Import Profile",
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFF667eea)
                            )
                            Text(
                                "Import",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF667eea).copy(alpha = 0.8f)
                            )
                        }
                    }
Spacer(modifier = Modifier.padding( 5.dp))
// Astonishing Export Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFf093fb).copy(alpha = 0.1f),
                                        Color(0xFFf5576c).copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFf093fb).copy(alpha = 0.4f),
                                        Color(0xFFf5576c).copy(alpha = 0.3f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { createDocumentLauncher.launch("mindflow_profile_backup.csv") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                Icons.Default.FileDownload,
                                contentDescription = "Export Profile",
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFFf093fb)
                            )
                            Text(
                                "Export",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFf093fb).copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        },
        bottomBar = {
            // --- UI TWEAK: Simplified bottom bar to only contain the Save button ---
            ActionButtons(
                onSave = {
                    profileViewModel.saveProfile()
                    Toast.makeText(context, "Profile Saved! 🎉", Toast.LENGTH_SHORT).show()
                }
            )
        }
    ) { paddingValues ->
        profile?.let { userProfile ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                CategorySelector(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                AnimatedContent(
                    targetState = selectedCategory,
                    transitionSpec = {
                        slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                    },
                    label = "category_transition"
                ) { category ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp), // UI Tweak: Reduced padding
                        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp) // UI Tweak: Reduced padding
                    ) {
                        item {
                            ProfileFormByCategory(
                                profile = userProfile,
                                category = category,
                                onProfileChange = { property, value ->
                                    profileViewModel.onProfileChange(property, value)
                                }
                            )
                        }
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// --- UI SUB-COMPONENTS ---

@Composable
fun CategorySelector(
    selectedCategory: ProfileCategory,
    onCategorySelected: (ProfileCategory) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp), // UI Tweak: Reduced padding
        horizontalArrangement = Arrangement.spacedBy(8.dp), // UI Tweak: Reduced spacing
        contentPadding = PaddingValues(horizontal = 16.dp) // UI Tweak: Reduced padding
    ) {
        items(ProfileCategory.values()) { category ->
            CategoryPill(
                category = category,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryPill(category: ProfileCategory, isSelected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f, label = "pill_scale")
    val color = if (isSelected) category.color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Button(
        onClick = onClick,
        modifier = Modifier.scale(scale),
        shape = RoundedCornerShape(16.dp), // UI Tweak: Smaller corner radius
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) category.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            contentColor = color
        ),
        border = BorderStroke(1.dp, if (isSelected) category.color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp) // UI Tweak: Reduced spacing
        ) {
            Icon(category.icon, contentDescription = null, modifier = Modifier.size(16.dp)) // UI Tweak: Smaller icon
            Text(category.displayName, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, fontSize = 13.sp) // UI Tweak: Smaller font
        }
    }
}

@Composable
fun ProfileFormByCategory(
    profile: UserProfile,
    category: ProfileCategory,
    onProfileChange: (KMutableProperty1<UserProfile, *>, Any) -> Unit
) {
    val fields = getFieldsByCategory(category)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { // UI Tweak: Reduced spacing
        // Category Header
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) { // UI Tweak: Reduced spacing
            Box(
                modifier = Modifier.size(36.dp).background(category.color.copy(alpha = 0.1f), CircleShape), // UI Tweak: Smaller box
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(20.dp)) // UI Tweak: Smaller icon
            }
            Text(category.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // Render fields based on their type
        fields.forEach { field ->
            when (field) {
                is FormField.Text -> EnhancedProfileTextField(
                    label = field.label,
                    value = field.property.get(profile),
                    onValueChange = { onProfileChange(field.property, it) },
                    icon = field.icon,
                    themeColor = category.color,
                    singleLine = field.singleLine
                )
                is FormField.Dropdown -> DropdownField(
                    label = field.label,
                    selectedValue = field.property.get(profile),
                    options = field.options,
                    onSelectionChanged = { onProfileChange(field.property, it) },
                    icon = field.icon,
                    themeColor = category.color
                )
                is FormField.Slider -> SliderField(
                    label = field.label,
                    value = field.property.get(profile),
                    onValueChange = { onProfileChange(field.property, it) },
                    range = field.range,
                    steps = field.steps,
                    unit = field.unit,
                    icon = field.icon,
                    themeColor = category.color
                )
                is FormField.Date -> DateField(
                    label = field.label,
                    value = field.property.get(profile),
                    onDateSelected = { onProfileChange(field.property, it) },
                    icon = field.icon,
                    themeColor = category.color
                )
                is FormField.Radio -> RadioGroupField(
                    label = field.label,
                    selectedValue = field.property.get(profile),
                    options = field.options,
                    onSelectionChanged = { onProfileChange(field.property, it) },
                    icon = field.icon,
                    themeColor = category.color
                )
            }
        }
    }
}

// --- CUSTOM INPUT FIELD COMPOSABLES ---

@Composable
fun EnhancedProfileTextField(
    label: String, value: String, onValueChange: (String) -> Unit, icon: ImageVector?,
    themeColor: Color, singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, fontSize = 14.sp) }, // UI Tweak: Smaller font
        leadingIcon = icon?.let { { Icon(it, contentDescription = null, Modifier.size(18.dp)) } }, // UI Tweak: Smaller icon
        singleLine = singleLine,
        shape = RoundedCornerShape(10.dp), // UI Tweak: Smaller corner radius
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = themeColor,
            focusedLabelColor = themeColor,
            focusedLeadingIconColor = themeColor,
            cursorColor = themeColor,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedContainerColor = themeColor.copy(alpha = 0.08f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String, selectedValue: String, options: List<String>, onSelectionChanged: (String) -> Unit,
    icon: ImageVector?, themeColor: Color
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 14.sp) }, // UI Tweak: Smaller font
            leadingIcon = icon?.let { { Icon(it, contentDescription = null, Modifier.size(18.dp)) } }, // UI Tweak: Smaller icon
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(10.dp), // UI Tweak: Smaller corner radius
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor,
                focusedLabelColor = themeColor,
                focusedLeadingIconColor = themeColor,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = themeColor.copy(alpha = 0.08f),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectionChanged(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SliderField(
    label: String, value: Float, onValueChange: (Float) -> Unit, range: ClosedFloatingPointRange<Float>,
    steps: Int, unit: String, icon: ImageVector?, themeColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)) // UI Tweak: Smaller corner radius
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 6.dp) // UI Tweak: Reduced padding
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                icon?.let { Icon(it, contentDescription = null, tint = themeColor, modifier = Modifier.size(18.dp)) } // UI Tweak: Smaller icon
                Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) // UI Tweak: Smaller font
            }
            Text("${value.roundToInt()} $unit", fontWeight = FontWeight.Bold, color = themeColor)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(thumbColor = themeColor, activeTrackColor = themeColor)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(label: String, value: String, onDateSelected: (String) -> Unit, icon: ImageVector?, themeColor: Color) {
    // --- FIX: Revamped Date Picker logic ---
    var showDatePicker by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.US) }

    // This will open the date picker
    if (showDatePicker) {
        val initialDateMillis = remember(value) {
            try {
                formatter.parse(value)?.time
            } catch (e: Exception) {
                // If parsing fails (e.g., empty string), default to today
                System.currentTimeMillis()
            }
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val selectedDate = Date(it)
                        // The formatter needs to account for the local timezone when formatting from UTC millis
                        val displayFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        onDateSelected(displayFormatter.format(selectedDate))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // This is the text field that shows the date and triggers the picker
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label, fontSize = 14.sp) }, // UI Tweak: Smaller font
        leadingIcon = icon?.let { { Icon(it, contentDescription = null, Modifier.size(18.dp)) } }, // UI Tweak: Smaller icon
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true }, // Click to show dialog
        shape = RoundedCornerShape(10.dp), // UI Tweak: Smaller corner radius
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = themeColor,
            focusedLabelColor = themeColor,
            focusedLeadingIconColor = themeColor,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedContainerColor = themeColor.copy(alpha = 0.08f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    )
}


@Composable
fun RadioGroupField(
    label: String, selectedValue: String, options: List<String>, onSelectionChanged: (String) -> Unit,
    icon: ImageVector?, themeColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)) // UI Tweak: Smaller corner radius
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 8.dp) // UI Tweak: Reduced padding
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            icon?.let { Icon(it, contentDescription = null, tint = themeColor, modifier = Modifier.size(18.dp)) } // UI Tweak: Smaller icon
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) // UI Tweak: Smaller font
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            options.forEach { option ->
                val isSelected = selectedValue == option
                val buttonColors = if (isSelected) {
                    ButtonDefaults.buttonColors(
                        containerColor = themeColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = { onSelectionChanged(option) },
                    shape = CircleShape,
                    colors = buttonColors,
                    border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp) // UI Tweak: Reduced padding
                ) {
                    Text(option, fontSize = 13.sp) // UI Tweak: Smaller font
                }
            }
        }
    }
}


// --- BOTTOM ACTION BUTTONS ---

@Composable
fun ActionButtons(onSave: () -> Unit) {
    // --- UI TWEAK: Removed Import/Export buttons from this component ---
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), // UI Tweak: Reduced padding
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().height(48.dp), // UI Tweak: Reduced height
                shape = RoundedCornerShape(14.dp), // UI Tweak: Smaller corner radius
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 3.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        brush = Brush.linearGradient(listOf(ProfileColors.Personal, ProfileColors.Personal.copy(alpha = 0.7f))),
                        shape = RoundedCornerShape(14.dp) // UI Tweak: Smaller corner radius
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Save")
                        Text("Save All Changes", fontSize = 15.sp, fontWeight = FontWeight.SemiBold) // UI Tweak: Smaller font
                    }
                }
            }
        }
    }
}

// --- DATA MAPPING FUNCTION ---
// (No changes were made to this function)
fun getFieldsByCategory(category: ProfileCategory): List<FormField> {
    return when (category) {
        ProfileCategory.PERSONAL -> listOf(
            FormField.Text("Full Name", Icons.Outlined.Person, UserProfile::fullName),
            FormField.Date("Date of Birth", Icons.Outlined.Cake, UserProfile::dateOfBirth),
            FormField.Dropdown("Gender", Icons.Outlined.Wc, UserProfile::gender, listOf("Male", "Female", "Non-binary", "Prefer not to say")),
            FormField.Text("Occupation", Icons.Outlined.Work, UserProfile::occupation),
            FormField.Text("Location", Icons.Outlined.LocationOn, UserProfile::location),
            FormField.Text("Emergency Contact", Icons.Outlined.ContactEmergency, UserProfile::emergencyContactName),
            FormField.Text("Emergency Phone", Icons.Outlined.Phone, UserProfile::emergencyContactPhone, singleLine = true)
        )
        ProfileCategory.HEALTH -> listOf(
            FormField.Text("Allergies", Icons.Outlined.MedicalServices, UserProfile::allergies, singleLine = false),
            FormField.Text("Medical Conditions", Icons.Outlined.LocalHospital, UserProfile::medicalConditions, singleLine = false),
            FormField.Text("Medication Info", Icons.Outlined.Medication, UserProfile::medicationInfo, singleLine = false),
            FormField.Text("Common Stressors", Icons.Outlined.MoodBad, UserProfile::stressors, singleLine = false),
            FormField.Text("Coping Mechanisms", Icons.Outlined.SelfImprovement, UserProfile::copingMechanisms, singleLine = false)
        )
        ProfileCategory.LIFESTYLE -> listOf(
            FormField.Dropdown("Diet Type", Icons.Outlined.Restaurant, UserProfile::dietType, listOf("Omnivore", "Vegetarian", "Vegan", "Pescatarian", "Other")),
            FormField.Dropdown("Exercise Frequency", Icons.Outlined.FitnessCenter, UserProfile::exerciseFrequency, listOf("Daily", "3-5 times a week", "1-2 times a week", "Rarely")),
            FormField.Slider("Sleep Target", Icons.Outlined.Bedtime, UserProfile::sleepTarget, 4f..12f, 8, "hours"),
            FormField.Slider("Water Intake Target", Icons.Outlined.WaterDrop, UserProfile::waterIntakeTarget, 1f..6f, 10, "liters"),
            FormField.Radio("Chronotype", Icons.Outlined.WbSunny, UserProfile::morningPerson, listOf("Morning Person", "Night Owl"))
        )
        ProfileCategory.PREFERENCES -> listOf(
            FormField.Text("Hobbies", Icons.Outlined.Palette, UserProfile::hobbies, singleLine = false),
            FormField.Text("Music Preferences", Icons.Outlined.MusicNote, UserProfile::musicPreferences, singleLine = false),
            FormField.Text("Film/TV Preferences", Icons.Outlined.Movie, UserProfile::filmPreferences, singleLine = false),
            FormField.Text("Book Preferences", Icons.Outlined.MenuBook, UserProfile::bookPreferences, singleLine = false),
            FormField.Text("Pet Peeves", Icons.Outlined.ThumbDown, UserProfile::petPeeves, singleLine = false)
        )
        ProfileCategory.GOALS -> listOf(
            FormField.Text("Personal Goals", Icons.Outlined.Flag, UserProfile::personalGoals, singleLine = false),
            FormField.Text("Favorite Quote", Icons.Outlined.FormatQuote, UserProfile::favoriteQuote, singleLine = false),
            FormField.Text("Life Motto", Icons.Outlined.Lightbulb, UserProfile::lifeMotto, singleLine = false),
            FormField.Text("Proudest Moment", Icons.Outlined.EmojiEvents, UserProfile::proudestMoment, singleLine = false)
        )
    }
}
