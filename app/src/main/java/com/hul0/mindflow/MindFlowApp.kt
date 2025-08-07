// app/src/main/java/com/hul0/mindflow/MindFlowApp.kt
package com.hul0.mindflow

import android.app.Application
import com.hul0.mindflow.data.*
import com.hul0.mindflow.model.FunFact
import com.hul0.mindflow.model.MentalHealthTip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * The Application class for MindFlow.
 * This is the entry point of the app process and is a great place
 * for one-time initializations.
 */
class MindFlowApp : Application() {

    // A custom coroutine scope for application-level tasks.
    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Launch a coroutine to prepopulate the database on first launch.
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            // Get database instance
            val database = AppDatabase.getDatabase(this@MindFlowApp)

            // Initialize repositories
            val quotesRepository = QuotesRepository(database.quoteDao())
            val funFactRepository = FunFactRepository(database.funFactDao())
            val mentalHealthTipRepository = MentalHealthTipRepository(database.mentalHealthTipDao())

            // Pre-populate quotes if the table is empty
            if (quotesRepository.getAllQuotes().firstOrNull().isNullOrEmpty()) {
                quotesRepository.insertInitialQuotes(QuotesRepository.getInitialQuotes())
            }

            // Pre-populate fun facts if the table is empty
            if (database.funFactDao().getAllFunFacts().firstOrNull().isNullOrEmpty()) {
                val facts = listOf(
                    FunFact(1, "Honey never spoils."),
                    FunFact(2, "A group of flamingos is called a 'flamboyance'."),
                    FunFact(3, "The unicorn is the national animal of Scotland."),
                    FunFact(4, "Octopuses have three hearts."),
                    FunFact(5, "Bananas are berries, but strawberries aren't.")
                )
                database.funFactDao().insertAll(facts)
            }

            // Pre-populate mental health tips if the table is empty
            if (database.mentalHealthTipDao().getAllMentalHealthTips().firstOrNull().isNullOrEmpty()) {
                val tips = listOf(
                    MentalHealthTip(
                        1,
                        "Take 5 deep breaths, in through your nose and out through your mouth."
                    ),
                    MentalHealthTip(2, "Write down 3 things you are grateful for today."),
                    MentalHealthTip(3, "Go for a 10-minute walk outside."),
                    MentalHealthTip(4, "Disconnect from social media for an hour."),
                    MentalHealthTip(5, "Listen to a calming song.")
                )
                database.mentalHealthTipDao().insertAll(tips)
            }
        }
    }
}
