package com.example.civinews.data.network

import com.example.civinews.data.models.auth.ForgotPasswordRequest
import com.example.civinews.data.models.auth.LoginRequest
import com.example.civinews.data.models.auth.LoginResponse
import com.example.civinews.data.models.auth.RegisterRequest
import com.example.civinews.data.models.auth.RegisterResponse
import com.example.civinews.data.models.report.ReportDetailResponse
import com.example.civinews.data.models.report.ReportRequest
import com.example.civinews.data.models.report.ReportResponse
import com.example.civinews.data.models.report.StatusUpdateRequest
import com.example.civinews.data.models.user.CanalResponse
import com.example.civinews.data.models.user.MessageResponse
import com.example.civinews.data.models.user.NameUpdateRequest
import com.example.civinews.data.models.user.PasswordChangeRequest
import com.example.civinews.data.models.user.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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
    suspend fun updateReportStatus(
        @Path("id") id: String,
        @Body request: StatusUpdateRequest
    ): Response<Unit>

    @DELETE("noticias/{id}")
    suspend fun deleteReport(@Path("id") id: String): Response<Unit>

    @GET("users/me")
    suspend fun getUserProfile(): UserResponse

    @PATCH("users/me/name")
    suspend fun updateUserName(@Body request: NameUpdateRequest): Response<UserResponse>

    @GET("noticias/{id}")
    suspend fun getReportDetail(@Path("id") id: String): ReportDetailResponse

    @PATCH("users/me/password")
    suspend fun changePassword(@Body request: PasswordChangeRequest): Response<MessageResponse>

    @DELETE("users/me")
    suspend fun deleteAccount(): Response<MessageResponse>

    @POST("users/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>
}