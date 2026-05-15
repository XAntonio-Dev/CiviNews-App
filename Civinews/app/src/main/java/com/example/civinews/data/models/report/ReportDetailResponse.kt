package com.example.civinews.data.models.report

import com.google.gson.annotations.SerializedName

data class ReportDetailResponse(
    val id: String,
    val titulo: String,
    val contenido: String,
    @SerializedName("imagen_url") val imagenUrl: String?,
    val estado: String,
    val ubicacion: String?,
    val latitud: Double?,
    val longitud: Double?,
    @SerializedName("fecha_creacion") val fechaCreacion: String,
    @SerializedName("autor_nombre") val autorNombre: String,
    @SerializedName("canal_nombre") val canalNombre: String
)