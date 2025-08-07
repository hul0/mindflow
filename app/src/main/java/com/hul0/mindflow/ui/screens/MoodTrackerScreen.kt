package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.MindFlowApp
import com.hul0.mindflow.model.MoodEntry
import com.hul0.mindflow.ui.viewmodel.MoodTrackerViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodTrackerScreen() {
    val application = LocalContext.current.applicationContext as MindFlowApp
    // THE FIX: Pass both DAOs to the factory constructor
    val factory = ViewModelFactory(application.database.quoteDao(), application.database.moodDao())
    val moodViewModel: MoodTrackerViewModel = viewModel(factory = factory)

    var mood by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val moodHistory by moodViewModel.moodHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("How are you feeling?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = mood,
            onValueChange = { mood = it },
            label = { Text("e.g., Happy, Calm, Productive") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("A few notes? (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                moodViewModel.addMood(mood, note)
                mood = ""
                note = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Mood")
        }

        Spacer(Modifier.height(24.dp))
        Text("Your Mood History", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(moodHistory) { entry ->
                MoodHistoryCard(entry)
            }
        }
    }
}

@Composable
fun MoodHistoryCard(entry: MoodEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(entry.mood, style = MaterialTheme.typography.titleLarge)
            if (entry.note.isNotBlank()) {
                Text(entry.note, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(entry.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
