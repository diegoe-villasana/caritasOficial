package com.example.template2025.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class AdminSessionExpiredException(message: String) : Exception(message)
data class AdminLoginRequest(
    @SerializedName("user") val user: String,
    @SerializedName("password") val password: String
)
data class AdminLoginResponse(
    val success: Boolean,
    val userType: String,
    val token: String?
)

data class Reserva(
    @SerializedName("reserva_id") val id: Int,
    @SerializedName("posada_id") val posadaId: Int,
    @SerializedName("fecha_entrada") val fechaEntrada: String,
    @SerializedName("solicitante_nombre") val nombreSolicitante: String,
    @SerializedName("pais_iso") val paisIso: String,
    @SerializedName("telefono_e164") val telefono: String,
    @SerializedName("genero_solicitante") val generoSolicitante: String,
    @SerializedName("hombres_cnt") val hombresCount: Int,
    @SerializedName("mujeres_cnt") val mujeresCount: Int,
    @SerializedName("total_personas") val totalPersonas: Int,
    @SerializedName("qr_token") val qrToken: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class ReservasGetResponse(
    val success: Boolean,
    @SerializedName("msg") val message: String,
    val data: List<Reserva>
)

data class ReservaByIdResponse(
    val success: Boolean,
    @SerializedName("msg") val message: String,
    val data: Reserva
)

data class UpdateEstadoRequest(
    @SerializedName("new_status") val estado: String
)

data class UpdateStatusRequest(
    @SerializedName("new_status") val status: String
)

class AdminRepository {
    suspend fun login(user: String, password: String): Result<AdminLoginResponse> {
        return try {
            val request = AdminLoginRequest(user, password)
            val response = ApiClient.publicApi.loginAdmin(request)

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
        } catch (_: Exception) {
                Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }

    suspend fun getAllReservas(): Result<ReservasGetResponse> {
        return try {
            val response = ApiClient.api.adminGetAllReservas()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                if (response.code() == 401) {
                    throw AdminSessionExpiredException("Sesión expirada")
                }
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Result.failure(Exception(errorResponse.msg))
                } else {
                    Result.failure(Exception("Error desconocido."))
                }
            }
        } catch (e: AdminSessionExpiredException) {
            throw e
        } catch (_: Exception) {
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }

    suspend fun getPosadaReservas(posadaId: Int): Result<ReservasGetResponse> {
        return try {
            val response = ApiClient.api.adminGetPosadaReservas(posadaId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                if (response.code() == 401) {
                    throw AdminSessionExpiredException("Sesión expirada")
                }
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    Result.failure(Exception(errorResponse.msg))
                } else {
                    Result.failure(Exception("Ocurrió un error desconocido."))
                }
            }
        } catch (e: AdminSessionExpiredException) {
            throw e
        } catch (_: Exception) {
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }

    suspend fun getReservaById(reservaId: Int): Result<Reserva> {
        return try {
            val response = ApiClient.api.adminGetReservaById(reservaId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                if (response.code() == 401) {
                    throw AdminSessionExpiredException("Sesión expirada")
                }
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).msg
                } else {
                    "Error desconocido al buscar la reserva."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: AdminSessionExpiredException) {
            throw e
        } catch (_: Exception) {
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }

    suspend fun cancelReserva(reservaId: Int): Result<Unit> {
        return try {
            val response = ApiClient.api.adminCancelReserva(reservaId)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                if (response.code() == 401) {
                    throw AdminSessionExpiredException("Sesión expirada")
                }
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).msg
                } else {
                    "Error al cancelar la reserva."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: AdminSessionExpiredException) {
            throw e
        } catch (_: Exception) {
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }

    suspend fun updateReservaEstado(reservaId: Int, nuevoEstado: String): Result<Unit> {
        return try {
            val request = UpdateEstadoRequest(estado = nuevoEstado)
            val response = ApiClient.api.adminUpdateReservaEstado(reservaId, request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                if (response.code() == 401) {
                    throw AdminSessionExpiredException("Sesión expirada")
                }
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).msg
                } else {
                    "Error al actualizar el estado."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: AdminSessionExpiredException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }

    suspend fun updateReservationStatus(qrToken: String, newStatus: String): Result<Unit> {
        return try {
            val request = UpdateStatusRequest(newStatus)
            val response = ApiClient.api.updateReservationStatus(qrToken, request)

            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                if (response.code() == 401) {
                    throw AdminSessionExpiredException("Sesión expirada")
                }
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).msg
                } else {
                    "Error al actualizar el estado."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: AdminSessionExpiredException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar al servidor. Revisa tu conexión a internet."))
        }
    }
}