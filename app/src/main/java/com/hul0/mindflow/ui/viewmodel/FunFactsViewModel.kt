// app/src/main/java/com/hul0/mindflow/ui/viewmodel/FunFactsViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.FunFactRepository
import com.hul0.mindflow.model.FunFact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FunFactsViewModel(private val repository: FunFactRepository) : ViewModel() {
    private val _funFact = MutableStateFlow<FunFact?>(null)
    val funFact: StateFlow<FunFact?> = _funFact

    init {
        getNewFunFact()
    }

    fun getNewFunFact() {
        viewModelScope.launch {
            _funFact.value = repository.getRandomFunFact()
        }
    }
}