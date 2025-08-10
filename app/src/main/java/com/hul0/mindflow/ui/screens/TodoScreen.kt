package com.hul0.mindflow.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.model.TodoItem
import com.hul0.mindflow.ui.viewmodel.TodoViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// A dedicated object for a more vibrant and consistent color scheme.
object TodoColors {
    val Background = Color(0xFF1A1C20)
    val Surface = Color(0xFF252830)
    val Primary = Color(0xFF8A5FF7)
    val Secondary = Color(0xFF33B6E6)
    val Tertiary = Color(0xFFE91E63)
    val OnBackground = Color(0xFFEAEAEA)
    val OnSurface = Color(0xFFD1D1D1)
    val Completed = Color(0xFF4CAF50) // Green for completed tasks
    val Error = Color(0xFFF44336)

    val TaskColors = listOf(
        Primary,
        Secondary,
        Tertiary,
        Color(0xFF03A9F4), // Light Blue
        Color(0xFFFF9800)  // Orange
    )
}

// A more diverse list of icons for the confetti animation.
private val confettiIcons = listOf(
    Icons.Default.Star,
    Icons.Default.Favorite,
    Icons.Default.CheckCircle,
    Icons.Default.AutoAwesome,
    Icons.Default.ThumbUp,
    Icons.Default.EmojiEvents,
    Icons.Default.Celebration
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(viewModel: TodoViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    val todos by viewModel.allTodos.observeAsState(initial = emptyList())
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showConfetti by remember { mutableStateOf(false) }

    val completedCount = todos.count { it.isCompleted }
    val totalCount = todos.size
    val completionPercentage = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    // Trigger for the confetti animation.
    LaunchedEffect(completedCount, totalCount) {
        if (totalCount > 0 && completedCount == totalCount) {
            showConfetti = true
            delay(4000) // Let the animation play
            showConfetti = false
        }
    }

    val onAddTask = {
        if (text.isNotBlank()) {
            viewModel.insert(TodoItem(task = text))
            text = ""
            keyboardController?.hide()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TodoColors.Background)
                .verticalScroll(rememberScrollState()) // --- UPDATED --- Screen is now scrollable
                .padding(20.dp)
        ) {
            HeaderSection(
                completedCount = completedCount,
                totalCount = totalCount,
                completionPercentage = completionPercentage
            )
            Spacer(modifier = Modifier.height(32.dp))
            EnhancedInputSection(
                text = text,
                onTextChange = { text = it },
                onAddTask = onAddTask,
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Combine Quick Actions and Categories into one row
            if (todos.isNotEmpty()) {
                ActionsAndCategoriesSection(
                    viewModel = viewModel,
                    todos = todos,
                    completedCount = completedCount,
                    pendingCount = totalCount - completedCount
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // The list is sorted to place completed items at the bottom.
            EnhancedTaskList(
                todos = todos.sortedBy { it.isCompleted },
                viewModel = viewModel
            )
        }

        // The celebration overlay is now just the improved confetti.
        if (showConfetti) {
            ConfettiCelebration()
        }
    }
}

@Composable
fun HeaderSection(completedCount: Int, totalCount: Int, completionPercentage: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = completionPercentage,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "ProgressAnimation"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Checklist,
                        contentDescription = "Tasks Icon",
                        tint = TodoColors.Primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = TodoColors.OnBackground
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = if (totalCount == 0) "Ready to be productive? ✨"
                    else if (completedCount == totalCount) "All tasks completed! 🎉"
                    else "$completedCount of $totalCount completed",
                    style = MaterialTheme.typography.titleMedium,
                    color = TodoColors.OnBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 36.dp)
                )
            }

            if (totalCount > 0) {
                Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 6.dp,
                        color = if (completedCount == totalCount) TodoColors.Completed else TodoColors.Primary,
                        trackColor = TodoColors.Primary.copy(alpha = 0.1f)
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TodoColors.OnBackground
                    )
                }
            }
        }

        if (totalCount > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (completedCount == totalCount) TodoColors.Completed else TodoColors.Primary,
                trackColor = TodoColors.Primary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun EnhancedInputSection(text: String, onTextChange: (String) -> Unit, onAddTask: () -> Unit) {
    var isInputFocused by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isInputFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "InputScaleAnimation"
    )

    Card(
        modifier = Modifier.fillMaxWidth().scale(animatedScale),
        colors = CardDefaults.cardColors(containerColor = TodoColors.Surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TodoColors.OnSurface.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                label = { Text("What needs to be done?", color = TodoColors.OnSurface.copy(0.7f)) },
                placeholder = { Text("e.g., Walk the dog", color = TodoColors.OnSurface.copy(0.5f)) },
                modifier = Modifier.weight(1f).onFocusChanged { focusState -> isInputFocused = focusState.isFocused },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddTask() }),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TodoColors.Primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = TodoColors.Surface,
                    unfocusedContainerColor = TodoColors.Surface,
                    focusedTextColor = TodoColors.OnBackground,
                    cursorColor = TodoColors.Primary,
                    unfocusedTextColor = TodoColors.OnBackground
                ),
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = TodoColors.Secondary) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.size(48.dp)) {
                if(text.isNotBlank()) {
                    Button(
                        onClick = onAddTask,
                        shape = CircleShape,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TodoColors.Primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task", tint = TodoColors.OnBackground)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionsAndCategoriesSection(
    viewModel: TodoViewModel,
    todos: List<TodoItem>,
    completedCount: Int,
    pendingCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        InfoChip(
            icon = Icons.Outlined.Schedule,
            text = "$pendingCount Pending",
            color = TodoColors.Secondary
        )
        InfoChip(
            icon = Icons.Outlined.TaskAlt,
            text = "$completedCount Completed",
            color = TodoColors.Completed
        )
        if (todos.any { it.isCompleted }) {
            QuickActionChip(
                icon = Icons.Default.DeleteSweep,
                text = "Clear",
                color = TodoColors.Error,
                onClick = { todos.filter { it.isCompleted }.forEach { viewModel.delete(it) } }
            )
        }
    }
}

@Composable
fun QuickActionChip(icon: ImageVector, text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Text(text = text, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Text(text = text, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnhancedTaskList(todos: List<TodoItem>, viewModel: TodoViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        todos.forEachIndexed { index, todo ->
            EnhancedTodoItem(
                todo = todo,
                index = index,
                onCheckedChange = { isChecked -> viewModel.update(todo.copy(isCompleted = isChecked)) },
                onDelete = { viewModel.delete(todo) }
            )
        }
        if (todos.isEmpty()) {
            EmptyStateCard()
        }
    }
}

@Composable
fun EnhancedTodoItem(
    modifier: Modifier = Modifier,
    todo: TodoItem,
    index: Int,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val taskColor = TodoColors.TaskColors[index % TodoColors.TaskColors.size]

    val cardColor by animateColorAsState(
        targetValue = if (todo.isCompleted) TodoColors.Completed.copy(alpha = 0.15f) else TodoColors.Surface,
        animationSpec = spring(), label = "CardColorAnimation"
    )
    val borderColor by animateColorAsState(
        targetValue = if (todo.isCompleted) TodoColors.Completed.copy(alpha = 0.3f) else taskColor.copy(alpha = 0.2f),
        animationSpec = spring(), label = "BorderColorAnimation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(), // --- UPDATED --- Animates size changes smoothly
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                RoundCheckbox(
                    checked = todo.isCompleted,
                    onCheckedChange = onCheckedChange,
                    checkedColor = TodoColors.Completed,
                    uncheckedColor = taskColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = todo.task,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    color = if (todo.isCompleted) TodoColors.OnSurface.copy(alpha = 0.6f) else TodoColors.OnSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (todo.isCompleted) FontWeight.Normal else FontWeight.Medium
                )
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete",
                    tint = TodoColors.Error.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task", color = TodoColors.OnBackground) },
            text = { Text("Are you sure you want to delete this task?", color = TodoColors.OnSurface) },
            containerColor = TodoColors.Surface,
            confirmButton = {
                Button(
                    onClick = { onDelete(); showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TodoColors.Error)
                ) { Text("Delete", color = TodoColors.OnBackground) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    border = BorderStroke(1.dp, TodoColors.OnSurface.copy(0.5f))
                ) { Text("Cancel", color = TodoColors.OnSurface) }
            }
        )
    }
}

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedColor: Color,
    uncheckedColor: Color
) {
    val color by animateColorAsState(if (checked) checkedColor else Color.Transparent, label = "checkbox_bg_color")
    val borderColor by animateColorAsState(if (checked) checkedColor else uncheckedColor, label = "checkbox_border_color")
    val checkmarkColor by animateColorAsState(if (checked) TodoColors.Background else Color.Transparent, label = "checkmark_color")

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, borderColor, CircleShape)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
            exit = scaleOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeOut()
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Checked",
                tint = checkmarkColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = TodoColors.Primary.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, TodoColors.Primary.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.AssignmentTurnedIn,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TodoColors.Primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "All Clear!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = TodoColors.OnBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add a new task to get started.",
                style = MaterialTheme.typography.bodyMedium,
                color = TodoColors.OnSurface.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ConfettiCelebration() {
    val particles = remember { List(100) { Particle.create() } }

    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            var scale by remember { mutableStateOf(0f) }
            var alpha by remember { mutableStateOf(1f) }
            val yOffset by remember { mutableStateOf( (Random.nextFloat() * 200 - 100).dp ) }

            LaunchedEffect(Unit) {
                launch {
                    delay(particle.delay)
                    animate(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
                    ) { value, _ -> scale = value }

                    delay(2000)

                    animate(
                        initialValue = 1f,
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 1200, easing = FastOutLinearInEasing)
                    ) { value, _ -> alpha = value }
                }
            }

            Icon(
                imageVector = particle.icon,
                contentDescription = null,
                tint = particle.color,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = particle.x, y = particle.y + yOffset)
                    .scale(scale)
                    .alpha(alpha)
            )
        }
    }
}

private data class Particle(
    val x: Dp,
    val y: Dp,
    val color: Color,
    val icon: ImageVector,
    val delay: Long
) {
    companion object {
        fun create(): Particle {
            return Particle(
                x = Random.nextInt(-250, 250).dp,
                y = Random.nextInt(-450, 450).dp,
                color = TodoColors.TaskColors.random().copy(alpha = Random.nextFloat().coerceIn(0.5f, 1.0f)),
                icon = confettiIcons.random(),
                delay = Random.nextLong(0, 700)
            )
        }
    }
}
