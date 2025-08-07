// app/src/main/java/com/hul0/mindflow/model/FunFact.kt
package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fun_facts")
data class FunFact(
    @PrimaryKey val id: Int,
    val fact: String
)