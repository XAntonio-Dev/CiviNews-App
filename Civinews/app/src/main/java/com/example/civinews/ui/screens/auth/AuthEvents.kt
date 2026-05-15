package com.example.civinews.ui.screens.auth

data class AuthEvents(
    val onModeChange: (Boolean) -> Unit,
    val onUsernameChange: (String) -> Unit,
    val onEmailChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onSubmitClick: () -> Unit,
    val onShowForgotPassDialog: () -> Unit,
    val onDismissForgotPassDialog: () -> Unit,
    val onForgotPassEmailChange: (String) -> Unit,
    val onSubmitForgotPass: () -> Unit
)