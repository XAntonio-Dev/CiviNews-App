package com.example.civinews.data.model

data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val is_admin: Boolean
)