package com.example.civinews.ui.screens.home

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
class HomeViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    var state: HomeListState by mutableStateOf(HomeListState.Loading)
        private set

    // Backup de la lista original para no llamar a la API cada vez que filtramos
    private var allReports: List<ReportUiModel> = emptyList()

    init {
        getData()
    }

    // Maneja el cambio de chips y filtra la lista
    fun selectCategory(category: String) {
        val currentState = state
        if (currentState is HomeListState.Success) {
            val filteredList = if (category == "Todos") {
                allReports
            } else {
                allReports.filter { it.category.equals(category, ignoreCase = true) }
            }

            state = currentState.copy(
                selectedCategory = category,
                dataset = filteredList
            )
        }
    }

    fun getData() {
        viewModelScope.launch {
            state = HomeListState.Loading
            try {
                val noticias = repository.getNoticias()
                allReports = noticias

                val officialChannels = repository.getAvailableChannels()

                val dynamicCategories = if (officialChannels.isNotEmpty()) {
                    listOf("Todos") + officialChannels
                } else {
                    listOf("Todos") + noticias.map { it.category }.distinct().sorted()
                }

                if (noticias.isEmpty() && officialChannels.isEmpty()) {
                    state = HomeListState.NoData
                } else {
                    state = HomeListState.Success(
                        dataset = noticias,
                        categories = dynamicCategories
                    )
                }
            } catch (e: Exception) {
                state = HomeListState.Error(message = "Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}