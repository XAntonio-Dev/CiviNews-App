package com.example.civinews.ui.screens.addReport

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civinews.data.repository.ReportRepository
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AddReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {
    var state by mutableStateOf(AddReportState())
        private set

    init {
        fetchChannels()
    }

    private fun fetchChannels() {
        viewModelScope.launch {
            val fetchedCategories = repository.getAvailableChannels()
            if (fetchedCategories.isNotEmpty()) {
                state = state.copy(
                    categories = fetchedCategories,
                    category = fetchedCategories.first()
                )
            }
        }
    }

    fun onTitleChange(v: String) {
        state = state.copy(title = v, errorMessage = null)
    }

    fun onDetailsChange(v: String) {
        state = state.copy(details = v, errorMessage = null)
    }

    fun onCategoryChange(v: String) {
        state = state.copy(category = v)
    }

    fun onLocationTextChange(v: String) {
        state = state.copy(locationText = v)
    }

    fun onImageSelected(uri: Uri?) {
        state = state.copy(imageUri = uri)
    }

    fun onMapClick(point: Point) {
        state = state.copy(selectedPoint = point, errorMessage = null)
    }

    fun submit() {
        if (state.title.isBlank() || state.details.isBlank()) {
            state = state.copy(errorMessage = "Ups, faltan datos obligatorios")
            return
        }

        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)

            val lat = state.selectedPoint?.latitude() ?: 0.0
            val lng = state.selectedPoint?.longitude() ?: 0.0

            val result = repository.uploadReport(
                titulo = state.title,
                contenido = state.details,
                categoria = state.category,
                ubicacionTexto = state.locationText,
                latitud = lat,
                longitud = lng,
                imageUri = state.imageUri
            )

            result.fold(
                onSuccess = {
                    state = state.copy(isLoading = false, isSuccess = true)
                },
                onFailure = { error ->
                    state = state.copy(
                        isLoading = false,
                        errorMessage = "Error al enviar: ${error.localizedMessage}"
                    )
                }
            )
        }
    }

    fun searchAddress(query: String) {
        if (query.isBlank()) return

        val client = MapboxGeocoding.builder()
            .accessToken(com.example.civinews.BuildConfig.MAPBOX_TOKEN)
            .query(query)
            .limit(1)
            .build()

        client.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                val results = response.body()?.features()
                if (results != null && results.isNotEmpty()) {
                    val firstResult = results[0].center()
                    if (firstResult != null) {
                        state = state.copy(selectedPoint = firstResult)
                    }
                }
            }

            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                state = state.copy(errorMessage = "Error al buscar la dirección")
            }
        })
    }
}