package com.example.template2025.model

import com.google.gson.annotations.SerializedName

data class VoluntarioRegistroRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("posada_id") val posada_id: Int
)