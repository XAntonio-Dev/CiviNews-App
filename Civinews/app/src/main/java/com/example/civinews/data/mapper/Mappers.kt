package com.example.civinews.data.mapper

import com.example.civinews.data.models.ReportResponse
import com.example.civinews.ui.screens.home.ReportUiModel

fun ReportResponse.toUiModel(): ReportUiModel {
    // Traducimos el ID exacto de la base de datos al nombre real del canal
    val nombreCategoria = when (this.canalId) {
        1 -> "Tráfico y Movilidad"
        2 -> "Infraestructuras"
        3 -> "Limpieza y Medio Ambiente"
        4 -> "Seguridad Ciudadana"
        5 -> "Alerta Bulos"
        6 -> "Gasto Público"
        7 -> "Turismo y Convivencia"
        8 -> "Política Local"
        else -> "Otros"
    }

    return ReportUiModel(
        id = this.id,
        title = this.titulo,
        category = nombreCategoria,
        status = this.estado,
        time = formatFecha(this.fechaCreacion),
        location = this.ubicacion ?: "Ubicación desconocida",
        details = this.contenido,
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