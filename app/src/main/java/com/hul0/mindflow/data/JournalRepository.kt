// app/src/main/java/com/hul0/mindflow/data/JournalRepository.kt
package com.hul0.mindflow.data

import com.hul0.mindflow.model.JournalEntry
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalDao: JournalDao) {
    val allJournalEntries: Flow<List<JournalEntry>> = journalDao.getAllJournalEntries()

    suspend fun insert(entry: JournalEntry) {
        journalDao.insertJournalEntry(entry)
    }

    // Added delete function
    suspend fun delete(entry: JournalEntry) {
        journalDao.deleteJournalEntry(entry)
    }
}
