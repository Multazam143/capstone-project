package com.example.plantgard.api

import com.example.plantgard.network.PredictionResponse
import com.example.plantgard.ui.register.RegisterRequest
import com.example.plantgard.ui.login.LoginRequest
import com.example.plantgard.response.RegisterResponse
import com.example.plantgard.response.LoginResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @Multipart
    @POST("predicts")
    fun uploadImage(
        @Query("plants") plant: String,
        @HeaderMap headers: Map<String, String>,
        @Part file: MultipartBody.Part
    ): Call<PredictionResponse>
}
