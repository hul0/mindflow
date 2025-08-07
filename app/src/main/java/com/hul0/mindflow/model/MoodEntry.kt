package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mood: String, // e.g., "Happy", "Calm", "Anxious"
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)
