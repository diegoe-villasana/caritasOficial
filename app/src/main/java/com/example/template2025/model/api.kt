package com.example.template2025.model

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class ErrorResponse (
    val success: Boolean,
    val msg: String
)

interface BackendApi {
    @POST("admin/login")
    suspend fun loginAdmin(@Body request: AdminLoginRequest): Response<AdminLoginResponse>
}

object ApiClient {
    private const val BASE_URL = "https://back-end-caritas.onrender.com/api/"

    val api: BackendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)
    }
}