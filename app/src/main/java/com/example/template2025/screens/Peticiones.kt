package com.example.template2025.screens

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Peticiones.kt
data class ReservaRequest(
    val servicio: String,
    val num_tel: String,
    val fecha: String,
)

data class ReservaResponse(
    val success: Boolean,
    val msg: String
)

interface BackendApi {
    @POST("servicios")
    suspend fun enviarReserva(@Body request: ReservaRequest): ReservaResponse
}

object Peticiones {
    private const val BASE_URL = "https://back-end-caritas.onrender.com/api/"


    val api: BackendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)
    }
}
