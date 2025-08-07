// app/src/main/java/com/hul0/mindflow/model/JournalEntry.kt
package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val date: Long = System.currentTimeMillis()
)