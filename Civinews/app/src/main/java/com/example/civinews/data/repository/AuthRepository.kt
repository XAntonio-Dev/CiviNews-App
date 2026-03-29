package com.example.civinews.data.repository

import com.example.civinews.data.model.LoginRequest
import com.example.civinews.data.model.LoginResponse
import com.example.civinews.data.network.ApiService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(email: String, pass: String): LoginResponse {
        return apiService.login(LoginRequest(email, pass))
    }
}