package com.example.civinews.data.network

import com.example.civinews.data.models.CanalResponse
import com.example.civinews.data.models.LoginRequest
import com.example.civinews.data.models.LoginResponse
import com.example.civinews.data.models.RegisterRequest
import com.example.civinews.data.models.RegisterResponse
import com.example.civinews.data.models.ReportRequest
import com.example.civinews.data.models.ReportResponse
import com.example.civinews.data.models.StatusUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST


interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("canales")
    suspend fun getCanales(): List<CanalResponse>

    @GET("noticias")
    suspend fun getNoticias(): List<ReportResponse>

    @POST("noticias")
    suspend fun createReport(@Body request: ReportRequest): Response<Unit>

    @GET("noticias/pendientes")
    suspend fun getPendingNoticias(): List<ReportResponse>

    @GET("noticias/mis-avisos")
    suspend fun getMyReports(): List<ReportResponse>

    @PATCH("noticias/{id}/estado")
    suspend fun updateReportStatus(@retrofit2.http.Path("id") id: String, @Body request: StatusUpdateRequest): Response<Unit>

    @retrofit2.http.DELETE("noticias/{id}")
    suspend fun deleteReport(@retrofit2.http.Path("id") id: String): Response<Unit>
}