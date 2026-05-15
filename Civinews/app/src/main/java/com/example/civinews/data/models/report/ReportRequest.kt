package com.example.civinews.data.models.report

// DTO para el envío
data class ReportRequest(
    val titulo: String,
    val contenido: String,
    val categoria: String,
    val ubicacion_texto: String,
    val latitud: Double,
    val longitud: Double,
    val imagen_url: String?
)