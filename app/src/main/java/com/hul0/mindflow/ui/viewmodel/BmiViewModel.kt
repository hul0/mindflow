package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.pow

// Enum to represent the BMI category
enum class BmiCategory(val label: String) {
    None("—"),
    Underweight("Underweight"),
    Normal("Normal Weight"),
    Overweight("Overweight"),
    Obese("Obese")
}

// Data class to hold the entire UI state
data class BmiUiState(
    val height: String = "",
    val weight: String = "",
    val bmi: Float = 0.0f,
    val bmiCategory: BmiCategory = BmiCategory.None,
    val isHeightValid: Boolean = true,
    val isWeightValid: Boolean = true,
    val idealWeightRange: Pair<Float, Float> = 0f to 0f
)

class BmiViewModel : ViewModel() {

    // Private mutable state flows for height and weight inputs
    private val _height = MutableStateFlow("")
    private val _weight = MutableStateFlow("")

    // Private mutable state flow for the calculated BMI
    private val _bmi = MutableStateFlow(0.0f)

    // Combine all state into a single BmiUiState flow
    val uiState: StateFlow<BmiUiState> = combine(
        _height, _weight, _bmi
    ) { height, weight, bmi ->
        val heightFloat = height.toFloatOrNull()
        val weightFloat = weight.toFloatOrNull()

        val isHeightValid = heightFloat != null && heightFloat in 80f..250f || height.isBlank()
        val isWeightValid = weightFloat != null && weightFloat in 20f..300f || weight.isBlank()

        val category = getBmiCategory(bmi)
        val idealRange = calculateIdealWeightRange(heightFloat)

        BmiUiState(
            height = height,
            weight = weight,
            bmi = bmi,
            bmiCategory = category,
            isHeightValid = isHeightValid,
            isWeightValid = isWeightValid,
            idealWeightRange = idealRange
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BmiUiState()
    )

    /**
     * Updates the height value.
     * @param newHeight The new height string from the input field.
     */
    fun onHeightChange(newHeight: String) {
        _height.value = newHeight.filter { it.isDigit() || it == '.' }
    }

    /**
     * Updates the weight value.
     * @param newWeight The new weight string from the input field.
     */
    fun onWeightChange(newWeight: String) {
        _weight.value = newWeight.filter { it.isDigit() || it == '.' }
    }

    /**
     * Calculates the BMI based on the current height and weight.
     * It updates the internal _bmi state flow, which in turn updates the public uiState.
     */
    fun calculateBmi() {
        val heightFloat = _height.value.toFloatOrNull()
        val weightFloat = _weight.value.toFloatOrNull()

        if (heightFloat != null && heightFloat > 0 && weightFloat != null && weightFloat > 0) {
            _bmi.value = weightFloat / (heightFloat / 100).pow(2)
        } else {
            _bmi.value = 0.0f
        }
    }

    /**
     * Determines the BMI category based on the BMI value.
     * @param bmi The Body Mass Index value.
     * @return The corresponding BmiCategory.
     */
    private fun getBmiCategory(bmi: Float): BmiCategory {
        if (bmi <= 0f) return BmiCategory.None
        return when {
            bmi < 18.5f -> BmiCategory.Underweight
            bmi < 25f -> BmiCategory.Normal
            bmi < 30f -> BmiCategory.Overweight
            else -> BmiCategory.Obese
        }
    }

    /**
     * Calculates the ideal weight range for a given height.
     * @param heightCm The height in centimeters.
     * @return A Pair of Floats representing the min and max ideal weight in kg.
     */
    private fun calculateIdealWeightRange(heightCm: Float?): Pair<Float, Float> {
        if (heightCm == null || heightCm <= 0) return 0f to 0f
        val heightM = heightCm / 100f
        val minWeight = 18.5f * heightM.pow(2)
        val maxWeight = 24.9f * heightM.pow(2)
        return minWeight to maxWeight
    }
}
