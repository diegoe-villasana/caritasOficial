package com.example.template2025.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Posada(
    @SerializedName("posada_id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("imagen_url") val imageUrl: String,
    @SerializedName("is_activa") val isActive: Int,
    @SerializedName("capacidad_total") val capacidadTotal: Int,
    @SerializedName("capacidad_disponible") val capacidadDisponible: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class PosadasGetResponse(
    val success: Boolean,
    @SerializedName("msg") val message: String,
    val data: List<Posada>
)

class PosadaRepository {
    suspend fun getPosadas(): Result<List<Posada>> {
        return try {
            val response = ApiClient.publicApi.getPosadas()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Result.failure(Exception(errorResponse.msg))
                } else {
                    Result.failure(Exception("Ocurrió un error desconocido."))
                }
            }
        } catch (_: Exception) {
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }
}