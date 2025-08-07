package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hul0.mindflow.data.MoodDao
import com.hul0.mindflow.data.QuoteDao

class ViewModelFactory(
    private val quoteDao: QuoteDao,
    private val moodDao: MoodDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuotesViewModel(quoteDao) as T
        }
        if (modelClass.isAssignableFrom(MoodTrackerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoodTrackerViewModel(moodDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
