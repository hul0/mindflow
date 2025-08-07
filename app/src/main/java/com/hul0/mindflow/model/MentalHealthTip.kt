// app/src/main/java/com/hul0/mindflow/model/MentalHealthTip.kt
package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mental_health_tips")
data class MentalHealthTip(
    @PrimaryKey val id: Int,
    val tip: String
)
