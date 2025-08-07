// app/src/main/java/com/hul0/mindflow/data/SleepRepository.kt
package com.hul0.mindflow.data

import com.hul0.mindflow.model.SleepSession
import kotlinx.coroutines.flow.Flow

class SleepRepository(private val sleepDao: SleepDao) {
    val allSleepSessions: Flow<List<SleepSession>> = sleepDao.getAllSleepSessions()

    suspend fun insert(session: SleepSession) {
        sleepDao.insertSleepSession(session)
    }
}