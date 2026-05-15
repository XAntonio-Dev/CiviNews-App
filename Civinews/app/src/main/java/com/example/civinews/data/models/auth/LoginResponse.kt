package com.example.civinews.data.models.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    val user: UserDto
)

data class UserDto(
    val username: String,
    val email: String,
    @SerializedName("is_admin") val isAdmin: Boolean
)