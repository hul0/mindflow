// app/src/main/java/com/hul0/mindflow/data/JournalDao.kt
package com.hul0.mindflow.data

import androidx.room.*
import com.hul0.mindflow.model.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    // Added delete function
    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntry)
}
