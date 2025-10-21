package com.example.template2025.model



import com.example.template2025.screens.GuestScreenState
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.time.format.DateTimeFormatter
import java.time.LocalDate

data class CreateReservationRequest(
    @SerializedName("posadaId") val posadaId: Int,
    @SerializedName("fecha_entrada") val entryDate: String,
    @SerializedName("solicitante_nombre") val applicantName: String,
    @SerializedName("pais_iso") val countryIso: String,
    @SerializedName("telefono") val phone: String,
    @SerializedName("genero_solicitante") val applicantGender: String,
    @SerializedName("hombres_cnt") val menCount: Int,
    @SerializedName("mujeres_cnt") val womenCount: Int
)

data class CreateReservationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("reservationId") val reservationId: String?,
    @SerializedName("qrCodeUrl") val qrCodeUrl: String?, // URL para generar el QR
    @SerializedName("qr_token") val qr_token: String?
)

data class CheckReservationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("reservationStatus") val reservationStatus: String?, // "pendiente", "checkin", etc.
    @SerializedName("qrCodeUrl") val qrCodeUrl: String?
)

// 2. AÑADE la respuesta que esperamos
// Esta es la estructura que debería tener tu Request
data class CheckReservationRequest(
    @SerializedName("nombreCompleto") val fullName: String,
    @SerializedName("telefono") val phone: String,
    @SerializedName("codigoPais") val dialCode: String
)


class ReservationRepository {

    suspend fun createReservation(uiState: GuestScreenState): Result<CreateReservationResponse> {
        return try {

            // Formatear la fecha de DD/MM/AAAA a AAAA-MM-DD
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.parse(uiState.entryDate, inputFormatter)
            val formattedDate = date.format(outputFormatter)

            // Unir el código de país y el teléfono para formato E.164
            val phoneE164 =
                "${uiState.applicantInfo.country.dialCode}${uiState.applicantInfo.phone}"

            val request = CreateReservationRequest(
                posadaId = uiState.selectedPosada?.id?:0, // Usamos 'id' directamente, que ahora debe ser Int en Posadas
                entryDate = formattedDate,
                menCount = uiState.menCount,
                womenCount = uiState.womenCount,
                applicantName = uiState.applicantInfo.fullName,
                countryIso = uiState.applicantInfo.country.isoCode, // Nuevo campo que debemos añadir a la data class Country
                phone = phoneE164,
                applicantGender = uiState.applicantInfo.gender.lowercase() // Convertimos a minúsculas
            )

            // Llamar a la API.
            val response = ApiClient.api.createReservation(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).msg
                } else {
                    "Error desconocido al crear la reservación."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al procesar la solicitud: ${e.message}"))
        }
    }

    suspend fun checkReservation(
        fullName: String,
        phone: String,
        dialCode: String
    ): Result<CheckReservationResponse> {
        return try {
            val request = CheckReservationRequest(
                fullName = fullName,
                phone = phone,
                dialCode = dialCode
            )
            val response = ApiClient.api.checkReservation(
                fullName = fullName,
                phone = phone,
                dialCode = dialCode
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    Gson().fromJson(errorBody, ErrorResponse::class.java).msg
                } else {
                    "No se encontró una reservación con los datos proporcionados"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }

    }
}





