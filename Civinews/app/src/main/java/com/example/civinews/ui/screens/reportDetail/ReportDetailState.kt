package com.example.civinews.ui.screens.reportDetail

import com.example.civinews.data.models.report.ReportDetailResponse

sealed class ReportDetailState {
    object Loading : ReportDetailState()
    data class Error(val message: String) : ReportDetailState()
    data class Success(val report: ReportDetailResponse) : ReportDetailState()
}