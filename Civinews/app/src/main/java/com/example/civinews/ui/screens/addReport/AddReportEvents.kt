package com.example.civinews.ui.screens.addReport

import android.net.Uri
import com.mapbox.geojson.Point

data class AddReportEvents(
    val onTitleChange: (String) -> Unit,
    val onDetailsChange: (String) -> Unit,
    val onCategoryChange: (String) -> Unit,
    val onLocationTextChange: (String) -> Unit,
    val onImageSelected: (Uri?) -> Unit,
    val onSubmitClick: () -> Unit,
    val onMapClick: (Point) -> Unit,
    val onSearchLocation: (String) -> Unit,
)