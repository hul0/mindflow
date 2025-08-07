// app/src/main/java/com/hul0/mindflow/ui/viewmodel/QuotesViewModel.kt
package com.hul0.mindflow.ui.viewmodel

import androidx.lifecycle.*
import com.hul0.mindflow.data.QuotesRepository
import com.hul0.mindflow.model.Quote
import kotlinx.coroutines.launch

/**
 * The ViewModel for the Quotes screen.
 * It holds the UI state for the screen and handles business logic.
 * It interacts with the QuotesRepository to get and update data,
 * but it has no knowledge of the underlying data source (database, network, etc.).
 *
 * @param repository The repository that this ViewModel will use for data operations.
 */
class QuotesViewModel(private val repository: QuotesRepository) : ViewModel() {

    // Using asLiveData() to convert the Flow from the repository into LiveData.
    // The UI will observe this LiveData for changes.
    val allQuotes: LiveData<List<Quote>> = repository.getAllQuotes().asLiveData()
    val favoriteQuotes: LiveData<List<Quote>> = repository.getFavoriteQuotes().asLiveData()

    /**
     * Updates a quote's favorite status.
     * This function is called from the UI. It launches a coroutine in the
     * viewModelScope to call the repository's suspend function.
     *
     * @param quote The quote to be updated.
     */
    fun updateQuote(quote: Quote) {
        viewModelScope.launch {
            repository.updateQuote(quote)
        }
    }
}
