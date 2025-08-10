// app/src/main/java/com/hul0/mindflow/ui/screens/JournalScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.model.JournalEntry
import com.hul0.mindflow.ui.viewmodel.JournalViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// Enum for Mood types with associated emoji, color, and label
enum class MoodType(val emoji: String, val color: Color, val label: String) {
    AMAZING("🌟", Color(0xFF4CAF50), "Amazing"),
    HAPPY("😊", Color(0xFF8BC34A), "Happy"),
    OKAY("😐", Color(0xFFFF9800), "Okay"),
    SAD("😢", Color(0xFF2196F3), "Sad"),
    ANGRY("😠", Color(0xFFF44336), "Angry"),
    ANXIOUS("😰", Color(0xFF9C27B0), "Anxious")
}

// Enum for Journal categories with associated icon, color, and label
enum class JournalCategory(val icon: ImageVector, val color: Color, val label: String) {
    PERSONAL(Icons.Default.Person, Color(0xFF4CAF50), "Personal"),
    WORK(Icons.Default.Work, Color(0xFF2196F3), "Work"),
    TRAVEL(Icons.Default.Flight, Color(0xFFFF9800), "Travel"),
    HEALTH(Icons.Default.FavoriteBorder, Color(0xFFE91E63), "Health"),
    GOALS(Icons.Default.EmojiEvents, Color(0xFF9C27B0), "Goals"),
    GRATITUDE(Icons.Default.Star, Color(0xFF00D46F), "Gratitude")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(viewModel: JournalViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    // State variables for the screen
    var text by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<MoodType?>(null) }
    var selectedCategory by remember { mutableStateOf<JournalCategory?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var filterMood by remember { mutableStateOf<MoodType?>(null) }
    var filterCategory by remember { mutableStateOf<JournalCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showStats by remember { mutableStateOf(false) }
    var tagInput by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var entryToDelete by remember { mutableStateOf<JournalEntry?>(null) }


    val entries by viewModel.allJournalEntries.observeAsState(initial = emptyList())
    val dateFormatter = remember { SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    // Filter entries based on search query and selected filters
    val filteredEntries = remember(entries, searchQuery, filterMood, filterCategory) {
        entries.filter { entry ->
            val matchesSearch = searchQuery.isBlank() || entry.content.contains(searchQuery, ignoreCase = true) || entry.tags.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesMood = filterMood == null || entry.mood == filterMood!!.label
            val matchesCategory = filterCategory == null || entry.category == filterCategory!!.label
            matchesSearch && matchesMood && matchesCategory
        }.sortedByDescending { it.date }
    }

    // Dialog to confirm entry deletion
    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this journal entry?") },
            confirmButton = {
                Button(
                    onClick = {
                        entryToDelete?.let {
                            viewModel.delete(it)
                        }
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { entryToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Main layout for the journal screen
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    ) {
        // Header with title and action buttons
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Journal",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    // Stats button
                    IconButton(
                        onClick = { showStats = !showStats },
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = "Stats",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Filter button
                    IconButton(
                        onClick = { showFilters = !showFilters },
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, Color.Magenta.copy(alpha = 0.3f), CircleShape)
                            .background(Color.Magenta.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = Color.Magenta,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats section, shown if toggled
        if (showStats) {
            item {
                StatsSection(entries = entries)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search entries or tags...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Filters section, shown if toggled
        if (showFilters) {
            item {
                FiltersSection(
                    filterMood = filterMood,
                    onMoodFilterChange = { filterMood = it },
                    filterCategory = filterCategory,
                    onCategoryFilterChange = { filterCategory = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Mood selector
        item {
            Text(
                text = "How are you feeling?",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MoodType.values()) { mood ->
                    MoodChip(
                        mood = mood,
                        isSelected = selectedMood == mood,
                        onClick = { selectedMood = if (selectedMood == mood) null else mood }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Category selector
        item {
            Text(
                text = "Choose a category",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(JournalCategory.values()) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tag input field
        item {
            Text(
                text = "Add tags",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = { Text("e.g., #idea, #reflection", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (tagInput.isNotBlank()) {
                            tags = tags + tagInput.trim()
                            tagInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color.Green.copy(alpha = 0.3f), CircleShape)
                        .background(Color.Green.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Tag", tint = Color.Green)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (tags.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tags) { tag ->
                        TagChip(tag = tag, onRemove = { tags = tags - tag })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Main text input for the journal entry
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = {
                        Text(
                            "What's on your mind today? Share your thoughts, experiences, or reflections...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action buttons row with word count and save button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            viewModel.insert(
                                JournalEntry(
                                    content = text,
                                    mood = selectedMood?.label,
                                    category = selectedCategory?.label,
                                    tags = tags
                                )
                            )
                            text = ""
                            selectedMood = null
                            selectedCategory = null
                            tags = emptyList()
                        }
                    },
                    enabled = text.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save", fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Header for the list of entries
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Entries (${filteredEntries.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                if (filteredEntries.isNotEmpty()) {
                    Text(
                        text = "Latest First",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display list of entries or an empty state message
        if (filteredEntries.isEmpty()) {
            item {
                EmptyState(hasSearch = searchQuery.isNotBlank() || filterMood != null || filterCategory != null)
            }
        } else {
            items(filteredEntries, key = { it.id }) { entry ->
                JournalEntryCard(
                    entry = entry,
                    dateFormatter = dateFormatter,
                    timeFormatter = timeFormatter,
                    onDelete = { entryToDelete = entry }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Add final spacer for padding at the bottom of the list
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MoodChip(mood: MoodType, isSelected: Boolean, onClick: () -> Unit) {
    // A chip to display and select a mood. Colors are not themed as per instructions.
    val borderColor = if (isSelected) mood.color else mood.color.copy(alpha = 0.3f)
    val backgroundColor = if (isSelected) mood.color.copy(alpha = 0.2f) else mood.color.copy(alpha = 0.1f)
    val textColor = if (isSelected) mood.color else mood.color.copy(alpha = 0.8f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = mood.emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = mood.label,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun CategoryChip(category: JournalCategory, isSelected: Boolean, onClick: () -> Unit) {
    // A chip to display and select a category. Colors are not themed as per instructions.
    val borderColor = if (isSelected) category.color else category.color.copy(alpha = 0.3f)
    val backgroundColor = if (isSelected) category.color.copy(alpha = 0.2f) else category.color.copy(alpha = 0.1f)
    val iconColor = if (isSelected) category.color else category.color.copy(alpha = 0.8f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(category.icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = category.label,
            color = iconColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun TagChip(tag: String, onRemove: () -> Unit) {
    // A chip to display a tag with a remove button.
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "#$tag", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            Icons.Default.Close,
            contentDescription = "Remove Tag",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(16.dp)
                .clickable { onRemove() }
        )
    }
}

@Composable
fun StatsSection(entries: List<JournalEntry>) {
    // Section to display user statistics like entry count and streak.
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayEntries = entries.count { it.date >= today }
    val totalWords = entries.sumOf { it.content.split("\\s+".toRegex()).filter { w -> w.isNotBlank() }.size }
    val streak = calculateStreak(entries)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = todayEntries.toString(), label = "Today", color = MaterialTheme.colorScheme.primary)
            StatItem(value = entries.size.toString(), label = "Total", color = Color.Green)
            StatItem(value = totalWords.toString(), label = "Words", color = Color.Magenta)
            StatItem(value = "${streak}d", label = "Streak", color = MaterialTheme.colorScheme.primary)
        }
    }
}

fun calculateStreak(entries: List<JournalEntry>): Int {
    // Helper function to calculate the user's journaling streak.
    if (entries.isEmpty()) return 0

    val distinctDays = entries
        .map {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
        .distinct()
        .sortedDescending()

    var streak = 0
    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)

    val yesterday = Calendar.getInstance().apply {
        timeInMillis = today.timeInMillis
        add(Calendar.DAY_OF_YEAR, -1)
    }

    if (distinctDays.first() == today.timeInMillis || distinctDays.first() == yesterday.timeInMillis) {
        streak = if (distinctDays.contains(today.timeInMillis)) 1 else 0
        if (distinctDays.first() == yesterday.timeInMillis && !distinctDays.contains(today.timeInMillis)) {
            // Streak ended yesterday
        } else {
            if (!distinctDays.contains(today.timeInMillis)) streak = 0 // Does not have today's entry
            else streak = 1
        }

        if(streak > 0 || distinctDays.first() == yesterday.timeInMillis) {
            if(distinctDays.first() == yesterday.timeInMillis) streak = 1
            var lastDay = distinctDays.first()
            for (i in 1 until distinctDays.size) {
                val currentDay = distinctDays[i]
                val expectedPreviousDay = Calendar.getInstance().apply { timeInMillis = lastDay }
                expectedPreviousDay.add(Calendar.DAY_OF_YEAR, -1)

                if (currentDay == expectedPreviousDay.timeInMillis) {
                    streak++
                    lastDay = currentDay
                } else {
                    break
                }
            }
        }
    }
    return streak
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    // A single item for the stats section.
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FiltersSection(
    filterMood: MoodType?,
    onMoodFilterChange: (MoodType?) -> Unit,
    filterCategory: JournalCategory?,
    onCategoryFilterChange: (JournalCategory?) -> Unit
) {
    // A card containing filter options for mood and category.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Blue.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Blue.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Filter by Mood",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MoodType.values()) { mood ->
                    MoodChip(
                        mood = mood,
                        isSelected = filterMood == mood,
                        onClick = { onMoodFilterChange(if (filterMood == mood) null else mood) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Filter by Category",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(JournalCategory.values()) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = filterCategory == category,
                        onClick = { onCategoryFilterChange(if (filterCategory == category) null else category) }
                    )
                }
            }
        }
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    dateFormatter: SimpleDateFormat,
    timeFormatter: SimpleDateFormat,
    onDelete: () -> Unit
) {
    // A card to display a single journal entry.
    var showFullContent by remember { mutableStateOf(false) }
    val isLongContent = entry.content.length > 150
    val displayContent = if (showFullContent || !isLongContent) entry.content else entry.content.take(150) + "..."

    val mood = remember(entry.mood) { MoodType.values().find { it.label == entry.mood } }
    val category = remember(entry.category) { JournalCategory.values().find { it.label == entry.category } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dateFormatter.format(Date(entry.date)),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = timeFormatter.format(Date(entry.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, Color.Red.copy(alpha = 0.3f), CircleShape)
                        .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (mood != null || category != null) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    mood?.let {
                        Text(it.emoji, fontSize = 18.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(it.label, color = it.color, fontWeight = FontWeight.SemiBold)
                        if (category != null) Spacer(Modifier.width(12.dp))
                    }
                    category?.let {
                        Icon(it.icon, contentDescription = null, tint = it.color, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(it.label, color = it.color, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = displayContent,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            if (isLongContent) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (showFullContent) "Show less" else "Read more",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { showFullContent = !showFullContent }
                )
            }

            if (entry.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(entry.tags) { tag ->
                        Text(
                            text = "#$tag",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(hasSearch: Boolean) {
    // Composable to show when there are no journal entries or search results.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (hasSearch) Icons.Default.SearchOff else Icons.Default.Book,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasSearch) "No entries found" else "Start your journaling journey",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasSearch) "Try adjusting your search or filters" else "Share your thoughts, experiences, and reflections",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
