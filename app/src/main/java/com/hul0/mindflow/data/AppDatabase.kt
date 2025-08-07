// app/src/main/java/com/hul0/mindflow/data/AppDatabase.kt
package com.hul0.mindflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hul0.mindflow.model.*

@Database(entities = [Quote::class, MoodEntry::class, TodoItem::class, SleepSession::class, JournalEntry::class, FunFact::class, MentalHealthTip::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao
    abstract fun moodDao(): MoodDao
    abstract fun todoDao(): TodoDao
    abstract fun sleepDao(): SleepDao
    abstract fun journalDao(): JournalDao
    abstract fun funFactDao(): FunFactDao
    abstract fun mentalHealthTipDao(): MentalHealthTipDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindflow_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
