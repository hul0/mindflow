// app/src/main/java/com/hul0/mindflow/data/MentalHealthTipDao.kt
package com.hul0.mindflow.data

import androidx.room.*
import com.hul0.mindflow.model.MentalHealthTip
import kotlinx.coroutines.flow.Flow

@Dao
interface MentalHealthTipDao {
    @Query("SELECT * FROM mental_health_tips")
    fun getAllMentalHealthTips(): Flow<List<MentalHealthTip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tips: List<MentalHealthTip>)

    @Query("SELECT * FROM mental_health_tips ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomTip(): MentalHealthTip?
}
