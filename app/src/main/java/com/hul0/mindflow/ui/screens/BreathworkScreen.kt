// app/src/main/java/com/hul0/mindflow/ui/screens/BreathworkScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.ui.viewmodel.BreathworkViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun BreathworkScreen(viewModel: BreathworkViewModel = viewModel()) {
    val instruction by viewModel.instruction.collectAsState()
    val timer by viewModel.timer.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = instruction, fontSize = 32.sp)
        Text(text = timer, fontSize = 96.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Button(onClick = {
                job?.cancel()
                job = coroutineScope.launch {
                    viewModel.boxBreathing().collect()
                }
            }) {
                Text("4-4-4-4 Breathing")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                job?.cancel()
                job = coroutineScope.launch {
                    viewModel.fourFiveFourBreathing().collect()
                }
            }) {
                Text("4-5-4 Breathing")
            }
        }
        Button(onClick = {
            job?.cancel()
        }) {
            Text("Stop")
        }
    }
}
