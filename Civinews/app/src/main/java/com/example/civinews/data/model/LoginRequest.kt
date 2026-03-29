package com.example.civinews.data.model

// DTO para enviar datos al backend
data class LoginRequest(
    val email: String,
    val password: String
)