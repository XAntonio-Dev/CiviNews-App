package com.example.civinews.ui.screens.admin

import com.example.civinews.ui.screens.home.ReportUiModel

sealed class AdminListState {
    object Loading : AdminListState()
    object NoData : AdminListState()
    data class Error(val message: String) : AdminListState()
    data class Success(val dataset: List<ReportUiModel>) : AdminListState()
}