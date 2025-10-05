package com.example.template2025.model

class AdminRepository {

    suspend fun login(user: String, password: String): Result<AdminLoginResponse> {
        return try {
            val request = AdminLoginRequest(user, password)
            val response = ApiClient.api.loginAdmin(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}