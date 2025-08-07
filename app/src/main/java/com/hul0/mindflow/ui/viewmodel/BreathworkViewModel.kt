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
        emit(updateState("Get Ready...", "3"))
        delay(1000)
        emit(updateState("Get Ready...", "2"))
        delay(1000)
        emit(updateState("Get Ready...", "1"))
        delay(1000)

        while (true) {
            // Inhale - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }

            // Hold - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }

            // Exhale - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }

            // Hold - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }
        }
    }

    fun fourFiveFourBreathing() = flow {
        emit(updateState("Get Ready...", "3"))
        delay(1000)
        emit(updateState("Get Ready...", "2"))
        delay(1000)
        emit(updateState("Get Ready...", "1"))
        delay(1000)

        while (true) {
            // Inhale - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }

            // Hold - 5 seconds
            for (i in 5 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }

            // Exhale - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    fun powerBreathing() = flow {
        emit(updateState("Get Ready...", "3"))
        delay(1000)
        emit(updateState("Get Ready...", "2"))
        delay(1000)
        emit(updateState("Get Ready...", "1"))
        delay(1000)

        while (true) {
            // Inhale - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }

            // Hold - 7 seconds
            for (i in 7 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }

            // Exhale - 8 seconds
            for (i in 8 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    fun wimHofBreathing() = flow {
        emit(updateState("Get Ready...", "3"))
        delay(1000)
        emit(updateState("Get Ready...", "2"))
        delay(1000)
        emit(updateState("Get Ready...", "1"))
        delay(1000)

        // Wim Hof method: 30 quick breaths + retention
        var round = 1
        while (true) {
            emit(updateState("Round $round", "Begin"))
            delay(2000)

            // 30 quick breaths (2 seconds each)
            for (i in 1..30) {
                emit(updateState("Power Breathe", i.toString()))
                delay(2000)
            }

            emit(updateState("Last Breath", "Deep"))
            delay(3000)

            // Retention phase - 15 seconds minimum
            for (i in 15 downTo 1) {
                emit(updateState("Hold & Relax", "${i}s"))
                delay(1000)
            }

            emit(updateState("Recovery Breath", "Deep"))
            delay(3000)

            round++
            if (round > 3) round = 1 // Reset after 3 rounds
        }
    }

    fun triangleBreathing() = flow {
        emit(updateState("Get Ready...", "3"))
        delay(1000)
        emit(updateState("Get Ready...", "2"))
        delay(1000)
        emit(updateState("Get Ready...", "1"))
        delay(1000)

        while (true) {
            // Inhale - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }

            // Hold - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Hold", i.toString()))
                delay(1000)
            }

            // Exhale - 4 seconds
            for (i in 4 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    fun coherentBreathing() = flow {
        emit(updateState("Get Ready...", "3"))
        delay(1000)
        emit(updateState("Get Ready...", "2"))
        delay(1000)
        emit(updateState("Get Ready...", "1"))
        delay(1000)

        while (true) {
            // Inhale - 5 seconds
            for (i in 5 downTo 1) {
                emit(updateState("Breathe In", i.toString()))
                delay(1000)
            }

            // Exhale - 5 seconds
            for (i in 5 downTo 1) {
                emit(updateState("Breathe Out", i.toString()))
                delay(1000)
            }
        }
    }

    private fun updateState(instruction: String, timer: String): Pair<String, String> {
        _instruction.value = instruction
        _timer.value = timer
        return instruction to timer
    }
}