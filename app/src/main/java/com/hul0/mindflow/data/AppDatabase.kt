package com.hul0.mindflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hul0.mindflow.model.*

/**
 * The main database class for the application.
 * It defines the entities and provides access to the DAOs.
 */
@Database(entities = [
    Quote::class,
    FunFact::class,
    MentalHealthTip::class,
    JournalEntry::class,
    MoodEntry::class,
    SleepSession::class,
    TodoItem::class,
    UserProfile::class // Added UserProfile entity
], version = 3, exportSchema = false) // Incremented version number
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // DAOs for each entity
    abstract fun quoteDao(): QuoteDao
    abstract fun funFactDao(): FunFactDao
    abstract fun mentalHealthTipDao(): MentalHealthTipDao
    abstract fun journalDao(): JournalDao
    abstract fun moodDao(): MoodDao
    abstract fun sleepDao(): SleepDao
    abstract fun todoDao(): TodoDao
    abstract fun userProfileDao(): UserProfileDao // Added UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton instance of the AppDatabase.
         *
         * @param context The application context.
         * @return The singleton AppDatabase instance.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindflow_database"
                )
                    .fallbackToDestructiveMigration() // In a real app, you'd handle migrations properly
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
