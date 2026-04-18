package com.example.civinews.ui.screens.home

// Modelo de UI para representar los reportes en la lista.
data class ReportUiModel(
    val id: String,
    val title: String,
    val details: String,
    val category: String,
    val status: String,
    val time: String,
    val location: String,
    val imageUrl: String?
)