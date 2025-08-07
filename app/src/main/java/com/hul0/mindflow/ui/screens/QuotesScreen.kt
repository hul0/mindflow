package com.hul0.mindflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hul0.mindflow.MindFlowApp
import com.hul0.mindflow.ui.components.QuoteCard
import com.hul0.mindflow.ui.viewmodel.QuotesViewModel
import com.hul0.mindflow.ui.viewmodel.ViewModelFactory

@Composable
fun QuotesScreen() {
    val application = LocalContext.current.applicationContext as MindFlowApp
    val factory = ViewModelFactory(application.database.quoteDao(), application.database.moodDao())
    val quotesViewModel: QuotesViewModel = viewModel(factory = factory)

    val allQuotes by quotesViewModel.allQuotes.collectAsState()

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
                    isFavorite = quote.isFavorite,
                    onFavoriteClick = { quotesViewModel.toggleFavorite(quote) }
                )
            }
        }
    }
}
