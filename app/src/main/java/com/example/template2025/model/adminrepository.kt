package com.example.template2025.model

import android.util.Log.e
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class AdminLoginRequest(
    @SerializedName("user") val user: String,
    @SerializedName("password") val password: String
)
data class AdminLoginResponse(
    val success: Boolean,
    val userType: String,
    val token: String?
)

class AdminRepository {
    suspend fun login(user: String, password: String): Result<AdminLoginResponse> {
        return try {
            val request = AdminLoginRequest(user, password)
            val response = ApiClient.api.loginAdmin(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Result.failure(Exception(errorResponse.msg))
                } else {
                    Result.failure(Exception("Error desconocido."))
                }
            }
        } catch (e: Exception) {
                Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }
}