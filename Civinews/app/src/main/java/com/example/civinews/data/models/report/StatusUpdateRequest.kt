package com.example.civinews.data.models.report

import com.google.gson.annotations.SerializedName

data class StatusUpdateRequest(
    @SerializedName("estado") val nuevoEstado: String
)