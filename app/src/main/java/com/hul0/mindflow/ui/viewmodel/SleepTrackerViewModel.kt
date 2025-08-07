// app/src/main/java/com/hul0/mindflow/ui/viewmodel/SleepTrackerViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.SleepRepository
import com.hul0.mindflow.model.SleepSession
import kotlinx.coroutines.launch

class SleepTrackerViewModel(private val repository: SleepRepository) : ViewModel() {
    val allSleepSessions: LiveData<List<SleepSession>> = repository.allSleepSessions.asLiveData()

    fun insert(session: SleepSession) = viewModelScope.launch {
        repository.insert(session)
    }
}