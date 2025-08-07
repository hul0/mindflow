// app/src/main/java/com/hul0/mindflow/ui/viewmodel/ViewModelFactory.kt
package com.hul0.mindflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hul0.mindflow.data.*

/**
 * ViewModelFactory is responsible for creating instances of all ViewModels.
 * It ensures that ViewModels with constructor dependencies (like repositories)
 * are instantiated correctly. This is a crucial part of dependency injection.
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Get a single instance of the database for the factory to use.
        val db = AppDatabase.getDatabase(application)

        // Check which ViewModel needs to be created and provide its dependencies.
        return when {
            modelClass.isAssignableFrom(QuotesViewModel::class.java) -> {
                // Create QuotesRepository with its QuoteDao dependency.
                val repo = QuotesRepository(db.quoteDao())
                // Create QuotesViewModel with the repository.
                QuotesViewModel(repo) as T // <-- This line is now corrected
            }
            modelClass.isAssignableFrom(MoodTrackerViewModel::class.java) -> {
                MoodTrackerViewModel(db.moodDao()) as T
            }
            modelClass.isAssignableFrom(TodoViewModel::class.java) -> {
                val repo = TodoRepository(db.todoDao())
                TodoViewModel(repo) as T
            }
            modelClass.isAssignableFrom(SleepTrackerViewModel::class.java) -> {
                val repo = SleepRepository(db.sleepDao())
                SleepTrackerViewModel(repo) as T
            }
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> {
                val repo = JournalRepository(db.journalDao())
                JournalViewModel(repo) as T
            }
            modelClass.isAssignableFrom(FunFactsViewModel::class.java) -> {
                val repo = FunFactRepository(db.funFactDao())
                FunFactsViewModel(repo) as T
            }
            modelClass.isAssignableFrom(MentalHealthViewModel::class.java) -> {
                val repo = MentalHealthTipRepository(db.mentalHealthTipDao())
                MentalHealthViewModel(repo) as T
            }
            modelClass.isAssignableFrom(BmiViewModel::class.java) -> {
                BmiViewModel() as T
            }
            modelClass.isAssignableFrom(BreathworkViewModel::class.java) -> {
                BreathworkViewModel() as T
            }
            // If the ViewModel class is unknown, throw an exception.
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
