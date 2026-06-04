package com.example.civinews.data.repository

import com.example.civinews.data.models.user.MessageResponse
import com.example.civinews.data.models.user.NameUpdateRequest
import com.example.civinews.data.models.user.PasswordChangeRequest
import com.example.civinews.data.models.user.RoleUpdateResponse
import com.example.civinews.data.models.user.UserResponse
import com.example.civinews.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserProfile(): Result<UserResponse> {
        return try {
            val response = apiService.getUserProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserName(newName: String): Result<UserResponse> {
        return try {
            val request = NameUpdateRequest(name = newName)
            val response = apiService.updateUserName(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar el nombre. Código: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(oldPass: String, newPass: String): Result<MessageResponse> = withContext(Dispatchers.IO) {
        try {
            val request = PasswordChangeRequest(oldPassword = oldPass, newPassword = newPass)
            val response = apiService.changePassword(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Si el backend lanza HTTPException 400, caerá por aquí
                Result.failure(Exception("Contraseña actual incorrecta o fallo del servidor."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<MessageResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteAccount()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al eliminar la cuenta: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtiene la lista completa de usuarios
    suspend fun getAllUsers(): Result<List<UserResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar usuarios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Busca usuarios por alias o email
    suspend fun searchUsers(query: String): Result<List<UserResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchUsers(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en la búsqueda: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Alterna permisos de administrador
    suspend fun toggleUserRole(userId: String): Result<RoleUpdateResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.toggleUserRole(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se pudo modificar el rol (Asegúrate de no modificar tu propia cuenta)."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Banea y elimina al usuario de la plataforma
    suspend fun banUser(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.banUser(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al banear usuario."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}