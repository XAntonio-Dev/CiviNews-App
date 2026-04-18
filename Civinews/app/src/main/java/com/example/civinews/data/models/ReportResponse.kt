package com.example.civinews.data.models

import com.google.gson.annotations.SerializedName

data class ReportResponse(
    val id: String,
    val titulo: String,
    val contenido: String,
    @SerializedName("imagen_url") val imagenUrl: String?,
    val estado: String,
    val ubicacion: String?,
    @SerializedName("fecha_creacion") val fechaCreacion: String,
    @SerializedName("canal_id") val canalId: Int?
)