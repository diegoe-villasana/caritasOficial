package com.example.template2025.model

class VolunteerRepo {

    suspend fun login(num_tel: String, password: String): Result<VolunteerResponse> {
        return try {
            val request = VolunteerLoginRequest(num_tel, password)
            val response = ApiClient.api.loginVolunteer(request)
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