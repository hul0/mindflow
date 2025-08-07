package com.hul0.mindflow.data

import com.hul0.mindflow.model.Quote

// This repository now acts as a source for the initial data.
// In a real app, this might come from a bundled JSON file or a network call.
object QuotesRepository {
    fun getInitialQuotes(): List<Quote> {
        // This is the same list of quotes from your original file.
        // In the new architecture, we provide it here to pre-populate the database.
        return listOf(
            Quote("1", "The only way to do great work is to love what you do.", "Jobs", "Steve Jobs", "Productivity", false),
            Quote("2", "The mind is everything. What you think you become.", "Buddha", "Buddha", "Mindfulness", false),
            Quote("3", "Strive not to be a success, but rather to be of value.", "Einstein", "Albert Einstein", "Personal Growth", false),
            Quote("4", "The best way to predict the future is to create it.", "Drucker", "Peter Drucker", "Future-Proofing", false),
            Quote("5", "Your time is limited, don't waste it living someone else's life.", "Jobs", "Steve Jobs", "Personal Growth", false),
            Quote("6", "The journey of a thousand miles begins with a single step.", "Tzu", "Lao Tzu", "Action", false)
            // Add all other quotes here...
        )
    }
}
