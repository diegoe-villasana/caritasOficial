package com.example.template2025.model

import android.content.Context
import com.example.template2025.dataStore.AppDataStore
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

data class ErrorResponse (
    val success: Boolean,
    @SerializedName("msg") val msg: String
)

interface BackendApi {
    @POST("admin/login")
    suspend fun loginAdmin(@Body request: AdminLoginRequest): Response<AdminLoginResponse>

    @GET("posadas/get")
    suspend fun getPosadas(): Response<PosadasGetResponse>

    @GET("admin/reservas/get")
    suspend fun adminGetAllReservas(): Response<ReservasGetResponse>

    @GET("admin/reservas/posada/{posada_id}")
    suspend fun adminGetPosadaReservas(@Path("posada_id") posadaId: Int): Response<ReservasGetResponse>

    @GET("admin/reservas/id/{reserva_id}")
    suspend fun adminGetReservaById(@Path("reserva_id") reservaId: Int): Response<ReservaByIdResponse>

    @DELETE("admin/reservas/delete/{reserva_id}")
    suspend fun adminCancelReserva(@Path("reserva_id") reservaId: Int): Response<ErrorResponse> // It returns a simple success/error message
}

object ApiClient {
    private const val BASE_URL = "https://back-end-caritas.onrender.com/api/"
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val dataStore by lazy {
        appContext?.let { AppDataStore(it) } ?: throw IllegalStateException("ApiClient not initialized.")
    }

    private val authInterceptor = Interceptor { chain ->
        val token = runBlocking { dataStore.tokenFlow.first() }

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        chain.proceed(requestBuilder.build())
    }

    private val authenticatedHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val publicHttpClient = OkHttpClient.Builder().build()

    private val retrofitAuthenticated: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authenticatedHttpClient) // Uses the interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitPublic: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(publicHttpClient) // Does NOT use the interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: BackendApi by lazy {
        retrofitAuthenticated.create(BackendApi::class.java)
    }

    val publicApi: BackendApi by lazy {
        retrofitPublic.create(BackendApi::class.java)
    }
}