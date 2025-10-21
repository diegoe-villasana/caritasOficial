package com.example.template2025.screens

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class ReservaRequest(
    val servicio: String,
    val reserva: String,
    val idusuario: Int

)

data class ReservaResponse(
    val success: Boolean,
    val msg: String
)

interface BackendApi {
    @POST("reservas")
    suspend fun enviarReserva(@Body request: ReservaRequest): ReservaResponse
}

object Peticiones {
    private const val BASE_URL = "http://10.0.2.2:25000/api/"


    val api: BackendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)
    }
}
