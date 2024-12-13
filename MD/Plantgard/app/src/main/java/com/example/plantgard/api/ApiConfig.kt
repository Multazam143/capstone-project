package com.example.plantgard.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Konfigurasi Retrofit
object ApiConfig {
    private const val BASE_URL = "https://plantgard-api-684536012763.asia-southeast1.run.app/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL + "auths/") // Append "auths/" di sini
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}



