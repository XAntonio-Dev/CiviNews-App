package com.example.civinews.ui.screens.addReport

import android.net.Uri
import com.mapbox.geojson.Point

data class AddReportState(
    val title: String = "",
    val details: String = "",
    val category: String = "Todos",
    val locationText: String = "",
    val imageUri: Uri? = null,
    val selectedPoint: Point? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val categories: List<String> = emptyList(),
)