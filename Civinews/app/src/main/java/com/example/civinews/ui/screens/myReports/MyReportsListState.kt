package com.example.civinews.ui.screens.myReports

import com.example.civinews.ui.screens.home.ReportUiModel

sealed class MyReportsListState {
    object Loading : MyReportsListState()
    object NoData : MyReportsListState()
    data class Error(val message: String) : MyReportsListState()
    data class Success(val dataset: List<ReportUiModel>) : MyReportsListState()
}