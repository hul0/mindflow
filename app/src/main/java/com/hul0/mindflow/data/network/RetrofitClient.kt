package com.hul0.mindflow.data.network// In a file like network/RetrofitClient.kt

import retrofit2.Retrofit

object RetrofitClient {

    // A placeholder base URL is required, but it will be overridden by the @Url parameter.
    private const val BASE_URL = "https://localhost/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
            .create(ApiService::class.java)
    }
}