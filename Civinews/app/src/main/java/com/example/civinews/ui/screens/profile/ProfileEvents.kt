package com.example.civinews.ui.screens.profile

data class ProfileEvents(
    val onEditNameClick: () -> Unit,
    val onLogoutClick: () -> Unit,
    val onDismissDialogs: () -> Unit,
    val onNameInputChange: (String) -> Unit,
    val onSaveName: () -> Unit,
    val onNavigateToAboutUs: () -> Unit,
    val onNavigateToTerms: () -> Unit,
    val onNavigateToPrivacy: () -> Unit,
    val onNavigateToHelp: () -> Unit,
    val onLogoutConfirm: () -> Unit,
    val onShowPasswordDialog: () -> Unit,
    val onOldPasswordChange: (String) -> Unit,
    val onNewPasswordChange: (String) -> Unit,
    val onSavePassword: () -> Unit,
    val onShowDeleteDialog: () -> Unit,
    val onConfirmDelete: () -> Unit
)