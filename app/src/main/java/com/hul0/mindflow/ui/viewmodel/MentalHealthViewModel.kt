
// app/src/main/java/com/hul0/mindflow/ui/viewmodel/MentalHealthViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.MentalHealthTipRepository
import com.hul0.mindflow.model.MentalHealthTip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MentalHealthViewModel(private val repository: MentalHealthTipRepository) : ViewModel() {
    private val _tip = MutableStateFlow<MentalHealthTip?>(null)
    val tip: StateFlow<MentalHealthTip?> = _tip

    init {
        getNewTip()
    }

    fun getNewTip() {
        viewModelScope.launch {
            _tip.value = repository.getRandomTip()
        }
    }
}