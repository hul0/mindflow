
// app/src/main/java/com/hul0/mindflow/ui/viewmodel/BreathworkViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class BreathworkViewModel : ViewModel() {

    private val _timer = MutableStateFlow("Start")
    val timer: StateFlow<String> = _timer

    private val _instruction = MutableStateFlow("Ready?")
    val instruction: StateFlow<String> = _instruction


    fun boxBreathing() = flow {
        emit(updateState("Get Ready...", "Ready?"))
        delay(2000)
        while (true) {
            emit(updateState("Breathe In", "4"))
            delay(4000)
            emit(updateState("Hold", "4"))
            delay(4000)
            emit(updateState("Breathe Out", "4"))
            delay(4000)
            emit(updateState("Hold", "4"))
            delay(4000)
        }
    }

    fun fourFiveFourBreathing() = flow {
        emit(updateState("Get Ready...", "Ready?"))
        delay(2000)
        while (true) {
            emit(updateState("Breathe In", "4"))
            delay(4000)
            emit(updateState("Hold", "5"))
            delay(5000)
            emit(updateState("Breathe Out", "4"))
            delay(4000)
        }
    }

    private fun updateState(instruction: String, timer: String): Pair<String, String> {
        _instruction.value = instruction
        _timer.value = timer
        return instruction to timer
    }
}