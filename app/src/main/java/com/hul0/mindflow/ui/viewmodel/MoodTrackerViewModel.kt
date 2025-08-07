package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.MoodDao
import com.hul0.mindflow.model.MoodEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MoodTrackerViewModel(private val moodDao: MoodDao) : ViewModel() {

    val moodHistory = moodDao.getAllMoods()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun addMood(mood: String, note: String) {
        viewModelScope.launch {
            if (mood.isNotBlank()) {
                val newEntry = MoodEntry(mood = mood, note = note)
                moodDao.insertMood(newEntry)
            }
        }
    }
}
