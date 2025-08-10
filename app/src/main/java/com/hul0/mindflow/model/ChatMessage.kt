// hul0/mindflow/mindflow-420a1f3c6faf5a0e40f158d1d0e60c100c99aee9/app/src/main/java/com/hul0/mindflow/model/ChatMessage.kt
package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatRoom::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: Long,
    val message: String,
    val timestamp: Date,
    val isFromUser: Boolean
)
