package com.example.civinews.data.mapper

import com.example.civinews.data.model.ReportResponse
import com.example.civinews.ui.screens.home.ReportUiModel

fun ReportResponse.toUiModel(): ReportUiModel {
    // Traducimos el ID de la base de datos al texto para los chips
    val nombreCategoria = when (this.canalId) {
        1 -> "Seguridad"
        2 -> "Eventos"
        3 -> "Tráfico"
        else -> "Otros"
    }

    return ReportUiModel(
        id = this.id,
        title = this.titulo,
        category = nombreCategoria,
        status = this.estado,
        time = formatFecha(this.fechaCreacion),
        location = this.ubicacion ?: "Ubicación desconocida",
        imageUrl = this.imagenUrl
    )
}

private fun formatFecha(fechaIso: String?): String {
    if (fechaIso == null) return "Hace un momento"
    return try {
        val cleanIso = fechaIso.split(".")[0]
        val localDateTime = java.time.LocalDateTime.parse(cleanIso)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM, HH:mm", java.util.Locale.getDefault())
        localDateTime.format(formatter)
    } catch (e: Exception) {
        "Recién publicado"
    }
}