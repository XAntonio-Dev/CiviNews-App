package com.example.civinews.ui.screens.home

class HomeEvents(
    val onCategorySelected: (String) -> Unit,
    val onNewReportClick: () -> Unit,
    val onReportClick: (String) -> Unit
)