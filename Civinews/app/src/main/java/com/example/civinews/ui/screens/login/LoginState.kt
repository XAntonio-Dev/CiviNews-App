package com.example.civinews.ui.screens.login

data class LoginState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)