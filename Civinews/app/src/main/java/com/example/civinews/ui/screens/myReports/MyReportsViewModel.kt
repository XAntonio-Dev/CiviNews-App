package com.example.civinews.ui.screens.myReports

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
class MyReportsViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    var state by mutableStateOf<MyReportsListState>(MyReportsListState.Loading)
        private set

    init {
        loadMyReports()
    }

    fun loadMyReports() {
        viewModelScope.launch {
            state = MyReportsListState.Loading
            try {
                val result = repository.getMyReports()
                if (result.isEmpty()) {
                    state = MyReportsListState.NoData
                } else {
                    state = MyReportsListState.Success(result)
                }
            } catch (e: Exception) {
                state = MyReportsListState.Error(e.message ?: "Error al cargar tus avisos")
            }
        }
    }
}