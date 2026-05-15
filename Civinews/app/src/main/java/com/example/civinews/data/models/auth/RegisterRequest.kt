package com.example.civinews.data.models.auth

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)