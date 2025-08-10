package com.hul0.mindflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hul0.mindflow.data.*
import com.hul0.mindflow.data.remote.OpenRouterService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * ViewModelFactory is responsible for creating instances of all ViewModels.
 * It ensures that ViewModels with constructor dependencies (like repositories)
 * are instantiated correctly. This is a crucial part of dependency injection.
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    // Lazy initialization of the database. This instance is shared across the factory.
    private val db by lazy { AppDatabase.getDatabase(application) }

    // Lazy initialization of the network service for the Chat feature.
    private val openRouterService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(OpenRouterService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(OpenRouterService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check which ViewModel needs to be created and provide its dependencies.
        return when {
            modelClass.isAssignableFrom(QuotesViewModel::class.java) -> {
                val repo = QuotesRepository(db.quoteDao())
                QuotesViewModel(repo) as T
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
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                val repo = UserProfileRepository(db.userProfileDao())
                ProfileViewModel(repo) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                val repo = ChatRepository(db.chatDao(), openRouterService)
                ChatViewModel(repo) as T
            }
            // If the ViewModel class is unknown, throw an exception.
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
