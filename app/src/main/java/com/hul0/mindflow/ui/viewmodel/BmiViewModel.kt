
// app/src/main/java/com/hul0/mindflow/ui/viewmodel/BmiViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.pow

class BmiViewModel : ViewModel() {
    private val _bmi = MutableStateFlow(0.0f)
    val bmi: StateFlow<Float> = _bmi

    fun calculateBmi(height: Float, weight: Float) {
        if (height > 0 && weight > 0) {
            _bmi.value = weight / (height / 100).pow(2)
        }
    }
}