// app/src/main/java/com/hul0/mindflow/ui/screens/BmiCalculatorScreen.kt
package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.ui.viewmodel.BmiViewModel

@Composable
fun BmiCalculatorScreen(viewModel: BmiViewModel = viewModel()) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val bmi by viewModel.bmi.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
            viewModel.calculateBmi(height.toFloatOrNull() ?: 0f, weight.toFloatOrNull() ?: 0f)
        }) {
            Text("Calculate BMI")
        }
        if (bmi > 0) {
            Text(text = "Your BMI is %.2f".format(bmi))
        }
    }
}