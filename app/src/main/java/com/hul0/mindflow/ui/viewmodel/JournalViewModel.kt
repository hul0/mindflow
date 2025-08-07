
// app/src/main/java/com/hul0/mindflow/ui/viewmodel/JournalViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.JournalRepository
import com.hul0.mindflow.model.JournalEntry
import kotlinx.coroutines.launch

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {
    val allJournalEntries: LiveData<List<JournalEntry>> = repository.allJournalEntries.asLiveData()

    fun insert(entry: JournalEntry) = viewModelScope.launch {
        repository.insert(entry)
    }
}