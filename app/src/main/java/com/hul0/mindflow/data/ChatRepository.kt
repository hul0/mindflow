package com.hul0.mindflow.data

import android.util.Log
import com.hul0.mindflow.data.remote.OpenRouterRequest
import com.hul0.mindflow.data.remote.OpenRouterService
import com.hul0.mindflow.data.remote.RequestMessage
import com.hul0.mindflow.model.ChatMessage
import com.hul0.mindflow.model.ChatRoom
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ChatRepository(
    private val chatDao: ChatDao,
    private val openRouterService: OpenRouterService
) {
    fun getAllRooms(): Flow<List<ChatRoom>> = chatDao.getAllRooms()

    fun getMessagesForRoom(roomId: Long): Flow<List<ChatMessage>> = chatDao.getMessagesForRoom(roomId)

    suspend fun createNewRoom(name: String): Long {
        val room = ChatRoom(name = name, createdAt = Date())
        return chatDao.insertRoom(room)
    }

    suspend fun sendMessage(roomId: Long, message: String, isFromUser: Boolean) {
        val chatMessage = ChatMessage(
            roomId = roomId,
            message = message,
            timestamp = Date(),
            isFromUser = isFromUser
        )
        chatDao.insertMessage(chatMessage)
    }

    /**
     * Gets a response from the AI model.
     *
     * This function now provides detailed error messages for easier debugging.
     * It handles:
     * 1.  **Network Exceptions:** Catches exceptions during the API call and returns the error message.
     * 2.  **API Errors:** If the API returns a non-successful HTTP status (e.g., 401 Unauthorized, 429 Rate Limit), it returns the error code and message from the server.
     * 3.  **Empty Responses:** If the API call is successful but returns no content, it provides a specific message.
     */
    suspend fun getAIResponse(apiKey: String, history: List<ChatMessage>): String {
        val requestMessages = history.map {
            RequestMessage(
                role = if (it.isFromUser) "user" else "assistant",
                content = it.message
            )
        }
        // It's good practice to use a model that you know is stable.
        // The "free" tier models can sometimes be unreliable.
        val request = OpenRouterRequest(
            model = "openai/gpt-oss-20b:free", // Using a more standard model name
            messages = requestMessages
        )

        try {
            val response = openRouterService.getChatCompletion("Bearer $apiKey", request)

            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                // Return the content if it's not null or blank, otherwise return a specific error.
                return if (!content.isNullOrBlank()) {
                    content
                } else {
                    "API Error: Received a successful response but it was empty."
                }
            } else {
                // The server responded with an error (e.g., 401, 404, 429).
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string() ?: "No error details."
                val errorMessage = "API Error $errorCode: $errorBody"
                Log.e("ChatRepository", errorMessage)
                return errorMessage
            }
        } catch (e: Exception) {
            // An exception occurred, likely a network issue or a problem with Retrofit/JSON parsing.
            Log.e("ChatRepository", "Network or parsing error", e)
            return "Network Error: ${e.localizedMessage ?: "An unknown error occurred."}"
        }
    }

    suspend fun deleteRoom(roomId: Long) {
        chatDao.deleteChatRoom(roomId)
    }
}
