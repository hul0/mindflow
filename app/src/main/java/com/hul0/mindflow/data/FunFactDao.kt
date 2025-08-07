// app/src/main/java/com/hul0/mindflow/data/FunFactDao.kt
package com.hul0.mindflow.data

import androidx.room.*
import com.hul0.mindflow.model.FunFact
import kotlinx.coroutines.flow.Flow

@Dao
interface FunFactDao {
    @Query("SELECT * FROM fun_facts")
    fun getAllFunFacts(): Flow<List<FunFact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facts: List<FunFact>)

    @Query("SELECT * FROM fun_facts ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomFunFact(): FunFact?
}