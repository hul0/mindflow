// app/src/main/java/com/hul0/mindflow/data/SleepDao.kt
package com.hul0.mindflow.data

import androidx.room.*
import com.hul0.mindflow.model.SleepSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_sessions ORDER BY date DESC")
    fun getAllSleepSessions(): Flow<List<SleepSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepSession(session: SleepSession)
}