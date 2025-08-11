// hul0/mindflow/mindflow-420a1f3c6faf5a0e40f158d1d0e60c100c99aee9/app/src/main/java/com/hul0/mindflow/data/AppDatabase.kt
package com.hul0.mindflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hul0.mindflow.model.*

// Incremented version number due to schema change
@Database(entities = [
    JournalEntry::class,
    MoodEntry::class,
    TodoItem::class,
    UserProfile::class,
    SleepSession::class,
    Quote::class,
    FunFact::class,
    MentalHealthTip::class,
    ChatRoom::class,
    ChatMessage::class
], version = 6) // Updated version
@TypeConverters(Converters::class) // Added TypeConverters for the List<String>
abstract class AppDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun moodDao(): MoodDao
    abstract fun todoDao(): TodoDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun sleepDao(): SleepDao
    abstract fun quoteDao(): QuoteDao
    abstract fun funFactDao(): FunFactDao
    abstract fun mentalHealthTipDao(): MentalHealthTipDao
    abstract fun chatDao(): ChatDao

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