package com.example.civinews.data.repository

import com.example.civinews.data.models.LoginRequest
import com.example.civinews.data.models.LoginResponse
import com.example.civinews.data.models.RegisterRequest
import com.example.civinews.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun login(email: String, pass: String): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(LoginRequest(email = email, password = pass))

            // Verificamos explícitamente que sea un código 200 OK
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Si es un 401, lo capturamos aquí y forzamos el fallo
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, alias: String, pass: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(email = email, username = alias, password = pass)
            val response = api.register(request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al registrar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}