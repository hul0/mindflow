package com.hul0.mindflow

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hul0.mindflow.data.AppDatabase
import kotlinx.coroutines.flow.firstOrNull

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(context)
        val moodDao = database.moodDao()
        val journalDao = database.journalDao()
        val funFactDao = database.funFactDao()

        val funFact = funFactDao.getRandomFunFact()
        if (funFact != null) {
            showNotification(context, "Fun Fact", funFact.fact)
        }

        val lastMood = moodDao.getAllMoods().firstOrNull()?.firstOrNull()
        if (lastMood == null || System.currentTimeMillis() - lastMood.timestamp > 24 * 60 * 60 * 1000) {
            showNotification(context, "How was your day ?", "We would love to know!")
        }

        val lastJournal = journalDao.getAllJournalEntries().firstOrNull()?.firstOrNull()
        if (lastJournal == null || System.currentTimeMillis() - lastJournal.date > 24 * 60 * 60 * 1000) {
            showNotification(context, "What's on your mind?", "Take a moment to write in your journal.")
        }
        return Result.success()
    }
}