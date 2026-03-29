package com.example.civinews.data.network

import com.example.civinews.data.model.LoginRequest
import com.example.civinews.data.model.LoginResponse
import com.example.civinews.data.model.ReportResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Obtengo el listado completo de noticias para el feed principal.
    @GET("noticias")
    suspend fun getNoticias(): List<ReportResponse>
}