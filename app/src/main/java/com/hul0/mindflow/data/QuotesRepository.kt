package com.hul0.mindflow.data

import com.hul0.mindflow.model.Quote

/**
 * This object acts as a static data source.
 * Its only job is to provide the initial list of quotes
 * that will be inserted into the database when the app is first installed.
 */
object QuotesRepository {
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
