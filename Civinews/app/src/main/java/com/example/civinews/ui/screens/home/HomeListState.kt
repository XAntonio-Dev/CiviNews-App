package com.example.civinews.ui.screens.home

sealed class HomeListState {
    data object Loading : HomeListState()
    data object NoData : HomeListState()
    data class Success(
        val dataset: List<ReportUiModel>,
        val selectedCategory: String = "Todos" // Por defecto empezamos en 'Todos'
    ) : HomeListState()
    data class Error(val message: String) : HomeListState()
}