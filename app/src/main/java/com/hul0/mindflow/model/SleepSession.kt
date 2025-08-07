// app/src/main/java/com/hul0/mindflow/model/SleepSession.kt
package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_sessions")
data class SleepSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bedTime: Long,
    val wakeUpTime: Long,
    val date: Long = System.currentTimeMillis()
)