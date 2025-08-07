// app/src/main/java/com/hul0/mindflow/ui/screens/MoodTrackerScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.model.MoodEntry
import com.hul0.mindflow.ui.viewmodel.MoodTrackerViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MoodTrackerScreen(viewModel: MoodTrackerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    val moodHistory by viewModel.moodHistory.observeAsState(initial = emptyList())
    val moods = listOf("😄", "😊", "😐", "😔", "😠") // Happy, Content, Neutral, Sad, Angry

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("How are you feeling today?", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moods.forEach { mood ->
                Text(
                    text = mood,
                    fontSize = 48.sp,
                    modifier = Modifier.clickable {
                        viewModel.addMoodEntry(MoodEntry(mood = mood, timestamp = System.currentTimeMillis()))
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Mood History", fontSize = 20.sp)
        LazyColumn {
            items(moodHistory) { entry ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(entry.mood, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                        Text(sdf.format(Date(entry.timestamp)))
                    }
                }
            }
        }
    }
}
