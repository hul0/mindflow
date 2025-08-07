// app/src/main/java/com/hul0/mindflow/data/FunFactRepository.kt
package com.hul0.mindflow.data

import com.hul0.mindflow.model.FunFact
import kotlinx.coroutines.flow.firstOrNull

class FunFactRepository(private val funFactDao: FunFactDao) {
    suspend fun getRandomFunFact(): FunFact? {
        // Check if the database is empty before trying to insert initial data.
        // We use firstOrNull() to get the first emission from the Flow, which is the current list of facts.
        if (funFactDao.getAllFunFacts().firstOrNull().isNullOrEmpty()) {
            val facts = listOf(
                FunFact(1, "Honey never spoils."),
                FunFact(2, "A group of flamingos is called a 'flamboyance'."),
                FunFact(3, "The unicorn is the national animal of Scotland."),
                FunFact(4, "Octopuses have three hearts."),
                FunFact(5, "Bananas are berries, but strawberries aren't.")
            )
            funFactDao.insertAll(facts)
        }
        // Return a random fact from the database.
        return funFactDao.getRandomFunFact()
    }
}
