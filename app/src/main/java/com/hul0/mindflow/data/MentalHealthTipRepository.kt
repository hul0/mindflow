// app/src/main/java/com/hul0/mindflow/data/MentalHealthTipRepository.kt
package com.hul0.mindflow.data

import com.hul0.mindflow.model.MentalHealthTip
import kotlinx.coroutines.flow.firstOrNull

class MentalHealthTipRepository(private val mentalHealthTipDao: MentalHealthTipDao) {
    suspend fun getRandomTip(): MentalHealthTip? {
        // In a real app, you'd fetch these from an API or a prepopulated database
        if (mentalHealthTipDao.getAllMentalHealthTips().firstOrNull().isNullOrEmpty()) {
            val tips = listOf(
                MentalHealthTip(1, "Take 5 deep breaths, in through your nose and out through your mouth."),
                MentalHealthTip(2, "Write down 3 things you are grateful for today."),
                MentalHealthTip(3, "Go for a 10-minute walk outside."),
                MentalHealthTip(4, "Disconnect from social media for an hour."),
                MentalHealthTip(5, "Listen to a calming song.")
            )
            mentalHealthTipDao.insertAll(tips)
        }
        return mentalHealthTipDao.getRandomTip()
    }
}