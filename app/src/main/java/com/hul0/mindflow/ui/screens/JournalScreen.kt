
// app/src/main/java/com/hul0/mindflow/ui/screens/JournalScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.model.JournalEntry
import com.hul0.mindflow.ui.viewmodel.JournalViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalScreen(viewModel: JournalViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    var text by remember { mutableStateOf("") }
    val entries by viewModel.allJournalEntries.observeAsState(initial = emptyList())
    val dateFormatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())


    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("What's on your mind?") },
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (text.isNotBlank()) {
                viewModel.insert(JournalEntry(content = text))
                text = ""
            }
        }, modifier = Modifier.align(Alignment.End)) {
            Text("Save")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(entries) { entry ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = dateFormatter.format(Date(entry.date)), style = MaterialTheme.typography.titleSmall)
                        Text(entry.content)
                    }
                }
            }
        }
    }
}