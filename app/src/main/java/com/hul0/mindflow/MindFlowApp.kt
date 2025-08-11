package com.hul0.mindflow

import android.app.Application
import com.hul0.mindflow.data.*
import com.hul0.mindflow.model.FunFact
import com.hul0.mindflow.model.MentalHealthTip
// Make sure to import your Retrofit client and ApiService
import com.hul0.mindflow.data.network.RetrofitClient
import com.hul0.mindflow.ui.viewmodel.NotificationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.IOException

class MindFlowApp : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    // You no longer need a separate OkHttpClient instance here
    // private val client = OkHttpClient()

    override fun onCreate() {
        super.onCreate()
        delayedInit()
        val notificationViewModel = NotificationViewModel(this)
        notificationViewModel.createNotificationChannel()
        notificationViewModel.scheduleNotifications()
    }

    private fun delayedInit() {
        applicationScope.launch {
            val database = AppDatabase.getDatabase(this@MindFlowApp)
            val quotesRepository = QuotesRepository(database.quoteDao())

            // Pre-populate quotes if the table is empty
            if (quotesRepository.getAllQuotes().firstOrNull().isNullOrEmpty()) {
                quotesRepository.insertInitialQuotes(QuotesRepository.getInitialQuotes())
            }

            // Pre-populate fun facts from a CSV using Retrofit
            if (database.funFactDao().getAllFunFacts().firstOrNull().isNullOrEmpty()) {
                val factsUrl = "https://raw.githubusercontent.com/hul0/dataflow/refs/heads/main/facts.csv"
                val facts = fetchAndParseCsv(factsUrl) { id, text -> FunFact(id, text) }
                if (facts.isNotEmpty()) {
                    database.funFactDao().insertAll(facts)
                }
            }

            // Pre-populate mental health tips from a CSV using Retrofit
            if (database.mentalHealthTipDao().getAllMentalHealthTips().firstOrNull().isNullOrEmpty()) {
                val tipsUrl = "https://raw.githubusercontent.com/hul0/dataflow/refs/heads/main/tasks.csv"
                val tips = fetchAndParseCsv(tipsUrl) { id, text -> MentalHealthTip(id, text) }
                if (tips.isNotEmpty()) {
                    database.mentalHealthTipDao().insertAll(tips)
                }
            }
        }
    }

    /**
     * Fetches a CSV file using Retrofit and parses it into a list of data objects.
     */
    private suspend fun <T> fetchAndParseCsv(url: String, factory: (id: Int, text: String) -> T): List<T> {
        return try {
            val response = RetrofitClient.apiService.downloadFile(url)
            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                responseBody?.lines()
                    ?.filter { it.isNotBlank() }
                    ?.mapIndexed { index, line -> factory(index + 1, line.trim()) }
                    ?: emptyList()
            } else {
                // Log error for unsuccessful response
                println("Network request failed: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}