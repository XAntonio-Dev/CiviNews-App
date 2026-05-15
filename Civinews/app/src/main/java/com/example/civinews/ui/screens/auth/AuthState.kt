package com.example.civinews.ui.screens.auth

data class AuthState(
    val isLoginMode: Boolean = true,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val usernameError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val userIsAdmin: Boolean = false,
    val errorMessage: String? = null,
    val showForgotPassDialog: Boolean = false,
    val forgotPassEmail: String = ""
)