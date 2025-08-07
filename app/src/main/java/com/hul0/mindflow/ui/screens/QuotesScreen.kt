// app/src/main/java/com/hul0/mindflow/ui/screens/QuotesScreen.kt
package com.hul0.mindflow.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.ui.components.QuoteCard
import com.hul0.mindflow.ui.viewmodel.QuotesViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory

@Composable
fun QuotesScreen(
    // Use the standard way to get the ViewModel with a factory in Compose.
    // The factory now only needs the application context.
    viewModel: QuotesViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {

    // Observe the LiveData from the ViewModel as state.
    // Provide an empty list as the initial value to prevent nulls.
    val allQuotes by viewModel.allQuotes.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Daily Quotes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(allQuotes) { quote ->
                QuoteCard(
                    quote = quote,
                    isFavorite = quote.isFavorite, // This line was missing
                    // The onFavoriteClick lambda now correctly calls the ViewModel's updateQuote function.
                    // It passes a copy of the quote with the isFavorite property toggled.
                    onFavoriteClick = {
                        viewModel.updateQuote(quote.copy(isFavorite = !quote.isFavorite))
                    }
                )
            }
        }
    }
}
