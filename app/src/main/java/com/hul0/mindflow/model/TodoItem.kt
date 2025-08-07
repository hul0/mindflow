// app/src/main/java/com/hul0/mindflow/model/TodoItem.kt
package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val task: String,
    val isCompleted: Boolean = false
)





