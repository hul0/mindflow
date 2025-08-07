
// app/src/main/java/com/hul0/mindflow/ui/screens/MentalHealthTipsScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.ui.viewmodel.MentalHealthViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory

@Composable
fun MentalHealthTipsScreen(viewModel: MentalHealthViewModel = viewModel(factory = ViewModelFactory(LocalContext.current.applicationContext as android.app.Application))) {
    val tip by viewModel.tip.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = tip?.tip ?: "Loading tip...",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { viewModel.getNewTip() }) {
            Text("New Tip")
        }
    }
}