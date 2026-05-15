package com.example.civinews.data.mapper

import com.example.civinews.data.models.report.ReportResponse
import com.example.civinews.ui.screens.home.ReportUiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        time = formatFecha(this.fechaCreacion),
        location = this.ubicacion ?: "Ubicación desconocida",
        details = this.contenido,
        imageUrl = this.imagenUrl,
        status = this.estado
    )
}

// Queda privada para que solo el mapper la use internamente
private fun formatFecha(fechaIso: String?): String {
    if (fechaIso == null) return "Hace un momento"
    return try {
        val cleanIso = fechaIso.split(".")[0]
        val localDateTime = LocalDateTime.parse(cleanIso)
        val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale.getDefault())
        localDateTime.format(formatter)
    } catch (e: Exception) {
        "Recién publicado"
    }
}