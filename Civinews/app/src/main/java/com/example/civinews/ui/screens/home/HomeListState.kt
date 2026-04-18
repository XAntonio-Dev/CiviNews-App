package com.example.civinews.ui.screens.home

sealed class HomeListState {
    object Loading : HomeListState()
    object NoData : HomeListState()
    data class Error(val message: String) : HomeListState()
    data class Success(
        val dataset: List<ReportUiModel>,
        val selectedCategory: String = "Todos",
        val categories: List<String> = emptyList()
    ) : HomeListState()
}
