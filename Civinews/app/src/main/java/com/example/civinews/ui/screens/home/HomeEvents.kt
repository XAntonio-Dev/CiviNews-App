package com.example.civinews.ui.screens.home

data class HomeEvents(
    val onCategorySelected: (String) -> Unit,
    val onNewReportClick: () -> Unit
)