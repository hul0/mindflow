// hul0/mindflow/mindflow-420a1f3c6faf5a0e40f158d1d0e60c100c99aee9/app/src/main/java/com/hul0/mindflow/data/remote/OpenRouterService.kt
package com.hul0.mindflow.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: OpenRouterRequest
    ): Response<OpenRouterResponse>

    companion object {
        const val BASE_URL = "https://openrouter.ai/api/"
    }
}
