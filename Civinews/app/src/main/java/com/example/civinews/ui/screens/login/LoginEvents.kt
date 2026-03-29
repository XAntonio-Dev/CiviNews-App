package com.example.civinews.ui.screens.login

data class LoginEvents(
    val onEmailChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onLoginClick: () -> Unit
)