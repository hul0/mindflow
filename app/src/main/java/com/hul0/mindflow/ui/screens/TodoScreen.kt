package com.hul0.mindflow.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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

// Data class for TodoItem model (assuming this structure)
// data class TodoItem(val id: Int = 0, val task: String, val isCompleted: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(viewModel: TodoViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    val todos by viewModel.allTodos.observeAsState(initial = emptyList())
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showSuccessAnimation by remember { mutableStateOf(false) }

    val completedCount = todos.count { it.isCompleted }
    val totalCount = todos.size
    val completionPercentage = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    // Celebration animation trigger
    LaunchedEffect(completedCount, totalCount) {
        if (totalCount > 0 && completedCount == totalCount) {
            showSuccessAnimation = true
            delay(4000) // Let the animation play
            showSuccessAnimation = false
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
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            // Header with progress
            HeaderSection(
                completedCount = completedCount,
                totalCount = totalCount,
                completionPercentage = completionPercentage
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Enhanced input section
            EnhancedInputSection(
                text = text,
                onTextChange = { text = it },
                onAddTask = onAddTask,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick actions
            QuickActionsSection(viewModel = viewModel, todos = todos)

            Spacer(modifier = Modifier.height(20.dp))

            // Task categories
            if (todos.isNotEmpty()) {
                TaskCategoriesSection(
                    completedCount = completedCount,
                    pendingCount = totalCount - completedCount
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Enhanced task list
            EnhancedTaskList(
                todos = todos,
                viewModel = viewModel
            )
        }

        // Success celebration overlay
        AnimatedVisibility(
            visible = showSuccessAnimation,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            CelebrationOverlay()
        }
    }
}

@Composable
fun HeaderSection(
    completedCount: Int,
    totalCount: Int,
    completionPercentage: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = completionPercentage,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "ProgressAnimation"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "My Tasks",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (totalCount == 0) "Ready to be productive?"
                    else if (completedCount == totalCount) "All tasks completed!"
                    else "$completedCount of $totalCount completed",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            if (totalCount > 0) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 6.dp,
                        color = if (completedCount == totalCount) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        if (totalCount > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (completedCount == totalCount) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun EnhancedInputSection(
    text: String,
    onTextChange: (String) -> Unit,
    onAddTask: () -> Unit
) {
    var isInputFocused by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isInputFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "InputScaleAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                label = { Text("What needs to be done?") },
                placeholder = { Text("Add a new task...") },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        isInputFocused = focusState.isFocused
                    },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAddTask() }),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // The button is now in a fixed-size Box to prevent resizing the text field
            Box(modifier = Modifier.size(48.dp)) {
                // --- CHANGE ---
                // Replaced AnimatedVisibility with a standard `if` condition for stability.
                // This resolves the implicit receiver error.
                if (text.isNotBlank()) {
                    Button(
                        onClick = onAddTask,
                        shape = CircleShape,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(viewModel: TodoViewModel, todos: List<TodoItem>) {
    val hasCompleted = todos.any { it.isCompleted }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (hasCompleted) {
            QuickActionChip(
                icon = Icons.Default.DeleteSweep,
                text = "Clear Completed",
                color = MaterialTheme.colorScheme.error,
                onClick = {
                    todos.filter { it.isCompleted }.forEach { viewModel.delete(it) }
                }
            )
        }
    }
}

@Composable
fun QuickActionChip(
    icon: ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "ChipScaleAnimation"
    )

    Box(
        modifier = Modifier
            .scale(animatedScale)
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.1f))
            .border(
                1.dp,
                color.copy(alpha = 0.3f),
                RoundedCornerShape(20.dp)
            )
            .clickable {
                isPressed = true
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
fun TaskCategoriesSection(completedCount: Int, pendingCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CategoryCard(
            title = "Pending",
            count = pendingCount,
            icon = Icons.Outlined.Schedule,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        CategoryCard(
            title = "Completed",
            count = completedCount,
            icon = Icons.Outlined.CheckCircle,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CategoryCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EnhancedTaskList(
    todos: List<TodoItem>,
    viewModel: TodoViewModel
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(todos, key = { it.id }) { todo ->
            EnhancedTodoItem(
                todo = todo,
                onCheckedChange = { isChecked ->
                    viewModel.update(todo.copy(isCompleted = isChecked))
                },
                onDelete = { viewModel.delete(todo) }
            )
        }

        if (todos.isEmpty()) {
            item {
                EmptyStateCard()
            }
        }
    }
}

@Composable
fun EnhancedTodoItem(
    todo: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val cardColor by animateColorAsState(
        targetValue = if (todo.isCompleted) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
        else MaterialTheme.colorScheme.surface,
        animationSpec = spring(), label = "CardColorAnimation"
    )

    val borderColor by animateColorAsState(
        targetValue = if (todo.isCompleted) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        animationSpec = spring(), label = "BorderColorAnimation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.tertiary,
                        uncheckedColor = MaterialTheme.colorScheme.primary,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = todo.task,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                    color = if (todo.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (todo.isCompleted) FontWeight.Normal else FontWeight.Medium
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.TaskAlt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Tasks Yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your first task to get started!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun CelebrationOverlay() {
    val particles = remember { List(50) { Particle() } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        particles.forEach { particle ->
            var scale by remember { mutableStateOf(0f) }
            var alpha by remember { mutableStateOf(1f) }

            LaunchedEffect(Unit) {
                launch {
                    delay(particle.delay)
                    animate(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                    ) { value, _ -> scale = value }

                    delay(1500)

                    animate(
                        initialValue = 1f,
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 1000)
                    ) { value, _ -> alpha = value }
                }
            }

            Icon(
                imageVector = particle.icon,
                contentDescription = null,
                tint = particle.color,
                modifier = Modifier
                    .offset(x = particle.x, y = particle.y)
                    .scale(scale)
                    .alpha(alpha)
            )
        }

        var cardScale by remember { mutableStateOf(0.5f) }
        LaunchedEffect(Unit) {
            animate(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) { value, _ -> cardScale = value }
        }

        Card(
            modifier = Modifier.scale(cardScale),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Celebration,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Congratulations!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "You've completed all your tasks!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

private data class Particle(
    val x: Dp = Random.nextInt(-200, 200).dp,
    val y: Dp = Random.nextInt(-400, 400).dp,
    val color: Color = listOf(
        Color(0xFFf44336), Color(0xFFe91e63), Color(0xFF9c27b0), Color(0xFF673ab7),
        Color(0xFF3f51b5), Color(0xFF2196f3), Color(0xFF03a9f4), Color(0xFF00bcd4),
        Color(0xFF009688), Color(0xFF4caf50), Color(0xFF8bc34a), Color(0xFFcddc39),
        Color(0xFFffeb3b), Color(0xFFffc107), Color(0xFFff9800), Color(0xFFff5722)
    ).random(),
    val icon: ImageVector = listOf(Icons.Default.Star, Icons.Default.Circle, Icons.Default.Favorite).random(),
    val delay: Long = Random.nextLong(0, 500)
)
