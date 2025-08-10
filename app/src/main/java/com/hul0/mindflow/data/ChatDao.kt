// hul0/mindflow/mindflow-420a1f3c6faf5a0e40f158d1d0e60c100c99aee9/app/src/main/java/com/hul0/mindflow/data/ChatDao.kt
package com.hul0.mindflow.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hul0.mindflow.model.ChatMessage
import com.hul0.mindflow.model.ChatRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: ChatRoom): Long

    @Query("SELECT * FROM chat_rooms ORDER BY createdAt DESC")
    fun getAllRooms(): Flow<List<ChatRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_messages WHERE roomId = :roomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(roomId: Long): Flow<List<ChatMessage>>

    @Query("DELETE FROM chat_rooms WHERE id = :roomId")
    suspend fun deleteChatRoom(roomId: Long)
}
