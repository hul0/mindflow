// app/src/main/java/com/hul0/mindflow/ui/viewmodel/MoodTrackerViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.MoodDao
import com.hul0.mindflow.model.MoodEntry
import kotlinx.coroutines.launch

/**
 * The ViewModel for the MoodTracker screen.
 * It uses the MoodDao to interact with the database.
 *
 * @param moodDao The Data Access Object for mood entries.
 */
class MoodTrackerViewModel(private val moodDao: MoodDao) : ViewModel() {

    // Expose the mood history from the database as LiveData.
    // The UI will observe this for changes.
    val moodHistory: LiveData<List<MoodEntry>> = moodDao.getAllMoods().asLiveData()

    /**
     * Inserts a new mood entry into the database.
     * This is called from the UI when the user taps an emoji.
     */
    fun addMoodEntry(moodEntry: MoodEntry) {
        viewModelScope.launch {
            moodDao.insertMood(moodEntry)
        }
    }
}
