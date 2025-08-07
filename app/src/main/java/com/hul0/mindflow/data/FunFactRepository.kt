// app/src/main/java/com/hul0/mindflow/data/FunFactRepository.kt
package com.hul0.mindflow.data

import com.hul0.mindflow.model.FunFact

class FunFactRepository(private val funFactDao: FunFactDao) {
    suspend fun getRandomFunFact(): FunFact? {
        // In a real app, you'd fetch these from an API or a prepopulated database
        if (funFactDao.getAllFunFacts().kotlinx.coroutines.flow.firstOrNull().isNullOrEmpty()) {
            val facts = listOf(
                FunFact(1, "Honey never spoils."),
                FunFact(2, "A group of flamingos is called a 'flamboyance'."),
                FunFact(3, "The unicorn is the national animal of Scotland."),
                FunFact(4, "Octopuses have three hearts."),
                FunFact(5, "Bananas are berries, but strawberries aren't.")
            )
            funFactDao.insertAll(facts)
        }
        return funFactDao.getRandomFunFact()
    }
}