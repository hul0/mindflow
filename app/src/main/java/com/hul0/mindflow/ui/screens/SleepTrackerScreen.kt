// app/src/main/java/com/hul0/mindflow/ui/screens/SleepTrackerScreen.kt
package com.hul0.mindflow.ui.screens

import android.app.TimePickerDialog
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
import com.hul0.mindflow.model.SleepSession
import com.hul0.mindflow.ui.viewmodel.SleepTrackerViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SleepTrackerScreen(viewModel: SleepTrackerViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    val context = LocalContext.current
    var bedTime by remember { mutableStateOf<Calendar?>(null) }
    var wakeUpTime by remember { mutableStateOf<Calendar?>(null) }
    val sleepSessions by viewModel.allSleepSessions.observeAsState(initial = emptyList())

    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Button(onClick = {
            val cal = Calendar.getInstance()
            TimePickerDialog(context, { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                bedTime = cal
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }) {
            Text(bedTime?.let { timeFormatter.format(it.time) } ?: "Select Bed Time")
        }

        Button(onClick = {
            val cal = Calendar.getInstance()
            TimePickerDialog(context, { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                wakeUpTime = cal
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }) {
            Text(wakeUpTime?.let { timeFormatter.format(it.time) } ?: "Select Wake Up Time")
        }

        Button(
            onClick = {
                if (bedTime != null && wakeUpTime != null) {
                    viewModel.insert(SleepSession(bedTime = bedTime!!.timeInMillis, wakeUpTime = wakeUpTime!!.timeInMillis))
                }
            },
            enabled = bedTime != null && wakeUpTime != null
        ) {
            Text("Save Session")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(sleepSessions) { session ->
                val duration = session.wakeUpTime - session.bedTime
                val hours = duration / (1000 * 60 * 60)
                val minutes = (duration % (1000 * 60 * 60)) / (1000 * 60)
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Slept for: $hours hours and $minutes minutes")
                        Text("Bedtime: ${timeFormatter.format(Date(session.bedTime))}")
                        Text("Woke up: ${timeFormatter.format(Date(session.wakeUpTime))}")
                    }
                }
            }
        }
    }
}
