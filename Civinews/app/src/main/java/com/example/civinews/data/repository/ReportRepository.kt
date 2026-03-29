package com.example.civinews.data.repository

import com.example.civinews.data.mapper.toUiModel
import com.example.civinews.data.model.ReportResponse
import com.example.civinews.data.network.ApiService
import com.example.civinews.ui.screens.home.ReportUiModel
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getNoticias(): List<ReportUiModel> {
        val response = api.getNoticias()
        // Invocamos el mapper para cada elemento de la lista
        return response.map { it.toUiModel() }
    }
}