// app/src/main/java/com/hul0/mindflow/data/QuotesRepository.kt
package com.hul0.mindflow.data

import com.hul0.mindflow.model.Quote
import kotlinx.coroutines.flow.Flow

/**
 * This class acts as the single source of truth for quote data.
 * It abstracts the data source (in this case, the Room database) from the rest of the app.
 * The ViewModel will interact with this repository to get and update data.
 */
class QuotesRepository(private val quoteDao: QuoteDao) {

    /**
     * Retrieves all quotes from the database as a Flow.
     * The Flow will automatically emit a new list of quotes whenever the data changes.
     */
    fun getAllQuotes(): Flow<List<Quote>> {
        return quoteDao.getAllQuotes()
    }

    /**
     * Retrieves all favorite quotes from the database.
     */
    fun getFavoriteQuotes(): Flow<List<Quote>> {
        return quoteDao.getFavoriteQuotes()
    }

    /**
     * Updates a quote in the database.
     * This is a suspend function, so it must be called from a coroutine.
     * @param quote The quote to be updated.
     */
    suspend fun updateQuote(quote: Quote) {
        quoteDao.updateQuote(quote)
    }

    /**
     * Inserts the initial list of quotes into the database.
     * This should only be called once when the database is first created.
     * @param quotes The list of quotes to insert.
     */
    suspend fun insertInitialQuotes(quotes: List<Quote>) {
        quoteDao.insertAll(quotes)
    }

    companion object {
        /**
         * Provides the initial list of quotes to populate the database.
         */
        fun getInitialQuotes(): List<Quote> {
            return listOf(
                Quote("1", "The only way to do great work is to love what you do.", "Jobs", "Steve Jobs", "Productivity", false),
                Quote("2", "The mind is everything. What you think you become.", "Buddha", "Buddha", "Mindfulness", false),
                Quote("3", "Strive not to be a success, but rather to be of value.", "Einstein", "Albert Einstein", "Personal Growth", false),
                Quote("4", "The best way to predict the future is to create it.", "Drucker", "Peter Drucker", "Future-Proofing", false),
                Quote("5", "Your time is limited, don't waste it living someone else's life.", "Jobs", "Steve Jobs", "Personal Growth", false),
                Quote("6", "The journey of a thousand miles begins with a single step.", "Tzu", "Lao Tzu", "Action", false),
                Quote("7", "Believe you can and you're halfway there.", "Roosevelt", "Theodore Roosevelt", "Confidence", false),
                Quote("8", "Success is not final, failure is not fatal: it is the courage to continue that counts.", "Churchill", "Winston Churchill", "Resilience", false)
                // You can add all your other quotes here
            )
        }
    }
}
