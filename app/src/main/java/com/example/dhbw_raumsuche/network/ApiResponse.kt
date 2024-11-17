package com.example.dhbw_raumsuche.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    @SerialName("icals") val iCals: List<String>,
    @SerialName("updated_at") val updatedAt: String
)
