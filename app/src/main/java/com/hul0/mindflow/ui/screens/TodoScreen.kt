// app/src/main/java/com/hul0/mindflow/ui/screens/TodoScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.model.TodoItem
import com.hul0.mindflow.ui.viewmodel.TodoViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory

@Composable
fun TodoScreen(viewModel: TodoViewModel = viewModel(factory = ViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))) {
    val todos by viewModel.allTodos.observeAsState(initial = emptyList())
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("New Todo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (text.isNotBlank()) {
                viewModel.insert(TodoItem(task = text))
                text = ""
            }
        }, modifier = Modifier.align(Alignment.End)) {
            Text("Add")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(todos) { todo ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(todo.task, modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = todo.isCompleted,
                        onCheckedChange = {
                            viewModel.update(todo.copy(isCompleted = it))
                        }
                    )
                    IconButton(onClick = { viewModel.delete(todo) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Todo")
                    }
                }
            }
        }
    }
}

