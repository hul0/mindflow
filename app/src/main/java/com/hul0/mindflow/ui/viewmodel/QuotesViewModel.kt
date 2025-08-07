package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hul0.mindflow.data.QuoteDao
import com.hul0.mindflow.model.Quote
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// No longer needs to be an AndroidViewModel. We pass the dependency directly.
class QuotesViewModel(private val quoteDao: QuoteDao) : ViewModel() {

    val allQuotes = quoteDao.getAllQuotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val favoriteQuotes = quoteDao.getFavoriteQuotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch {
            val updatedQuote = quote.copy(isFavorite = !quote.isFavorite)
            quoteDao.updateQuote(updatedQuote)
        }
    }
}
