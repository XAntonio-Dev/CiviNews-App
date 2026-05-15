package com.example.civinews.data.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.civinews.data.mapper.toUiModel
import com.example.civinews.data.models.report.ReportDetailResponse
import com.example.civinews.data.models.report.ReportRequest
import com.example.civinews.data.models.report.StatusUpdateRequest
import com.example.civinews.data.network.ApiService
import com.example.civinews.ui.screens.home.ReportUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class ReportRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getNoticias(): List<ReportUiModel> = withContext(Dispatchers.IO) {
        api.getNoticias().map { it.toUiModel() }
    }

    suspend fun getAvailableChannels(): List<String> = withContext(Dispatchers.IO) {
        try {
            api.getCanales().map { it.nombre }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun uploadReport(titulo: String, contenido: String, categoria: String, ubicacionTexto: String, latitud: Double, longitud: Double, imageUri: Uri?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            var finalImageUrl: String? = null

            // A. Si hay foto, la subimos a Cloudinary y esperamos la URL segura
            if (imageUri != null) {
                finalImageUrl = uploadToCloudinary(imageUri)
            }

            // B. Construimos el JSON limpio
            val request = ReportRequest(
                titulo = titulo,
                contenido = contenido,
                categoria = categoria,
                ubicacion_texto = ubicacionTexto,
                latitud = latitud,
                longitud = longitud,
                imagen_url = finalImageUrl
            )

            // C. Disparamos a FastAPI
            val response = api.createReport(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error de API: ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función auxiliar para transformar el callback de Cloudinary en una Corrutina limpia
    private suspend fun uploadToCloudinary(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .unsigned("civinews_preset")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    // Extraemos la URL con HTTPS que nos devuelve Cloudinary
                    val url = resultData?.get("secure_url") as? String
                    continuation.resume(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    continuation.resume(null)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    continuation.resume(null)
                }
            }).dispatch()
    }

    suspend fun getPendingNoticias(): List<ReportUiModel> = withContext(Dispatchers.IO) {
        api.getPendingNoticias().map { it.toUiModel() }
    }

    suspend fun approveReport(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateReportStatus(id, StatusUpdateRequest("aprobada"))
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error al aprobar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReport(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteReport(id)
            if (response.isSuccessful) Result.success(Unit) else Result.failure(Exception("Error al eliminar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyReports(): List<ReportUiModel> {
        return withContext(Dispatchers.IO) {
            val response = api.getMyReports()
            response.map { it.toUiModel() }
        }
    }

    suspend fun getReportDetail(id: String): ReportDetailResponse = withContext(Dispatchers.IO) {
        api.getReportDetail(id)
    }
}