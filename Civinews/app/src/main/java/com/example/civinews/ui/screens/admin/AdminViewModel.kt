package com.example.civinews.ui.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civinews.data.repository.ReportRepository
import com.example.civinews.ui.screens.home.ReportUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    var state: AdminListState by mutableStateOf(AdminListState.Loading)
        private set

    init {
        loadPendingReports()
    }

    fun loadPendingReports() {
        viewModelScope.launch {
            state = AdminListState.Loading
            try {
                val pending = repository.getPendingNoticias()
                if (pending.isEmpty()) {
                    state = AdminListState.NoData
                } else {
                    state = AdminListState.Success(dataset = pending)
                }
            } catch (e: Exception) {
                state = AdminListState.Error(message = "Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    fun approveReport(id: String) {
        viewModelScope.launch {
            state = AdminListState.Loading
            delay(1000)
            val result = repository.approveReport(id)
            if (result.isSuccess) {
                loadPendingReports()
            } else {
                state = AdminListState.Error("No se pudo aprobar el reporte")
            }
        }
    }

    fun deleteReport(id: String) {
        viewModelScope.launch {
            state = AdminListState.Loading
            val result = repository.deleteReport(id)
            if (result.isSuccess) {
                loadPendingReports()
            } else {
                state = AdminListState.Error("No se pudo eliminar el reporte")
            }
        }
    }
}