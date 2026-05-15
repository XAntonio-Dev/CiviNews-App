package com.example.civinews.ui.screens.reportDetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civinews.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    var state: ReportDetailState by mutableStateOf(ReportDetailState.Loading)
        private set

    fun getReportDetail(reportId: String) {
        viewModelScope.launch {
            state = ReportDetailState.Loading
            try {
                val response = repository.getReportDetail(reportId)
                state = ReportDetailState.Success(report = response)
            } catch (e: Exception) {
                state = ReportDetailState.Error(
                    message = e.localizedMessage ?: "Error al cargar el detalle"
                )
            }
        }
    }
}