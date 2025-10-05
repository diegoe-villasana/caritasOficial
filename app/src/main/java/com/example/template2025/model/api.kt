package com.example.template2025.model

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class AdminLoginRequest(
    @SerializedName("user") val user: String,
    @SerializedName("password") val password: String
)
data class AdminLoginResponse(
    val success: Boolean,
    val userType: String,
    val token: String?
)

data class VolunteerLoginRequest(
    @SerializedName("num_tel") val num_tel: String,
    @SerializedName("password") val password: String
)

data class VolunteerResponse(
    val success: Boolean,
    val userType: String,
    val token: String?
)
interface BackendApi {
    @POST("admin/login")
    suspend fun loginAdmin(@Body request: AdminLoginRequest): Response<AdminLoginResponse>

    @POST("volunteer/login")
    suspend fun loginVolunteer(@Body request: VolunteerLoginRequest): Response<VolunteerResponse>
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