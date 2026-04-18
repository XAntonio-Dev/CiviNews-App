package com.example.civinews.data.models

// DTO para enviar datos al backend
data class LoginRequest(
    val email: String,
    val password: String
)