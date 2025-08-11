package com.hul0.mindflow.data.network   // In a file like network/ApiService.kt

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    /**
     * Downloads raw file content from a dynamic URL.
     */
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>
}