package com.example.civinews.ui.screens.profile

data class ProfileState(
    val isLoading: Boolean = false,
    val currentName: String = "",
    val currentEmail: String = "",
    val isAdmin: Boolean = false,
    val showEditNameDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val newNameInput: String = "",
    val oldPasswordInput: String = "",
    val newPasswordInput: String = "",
    val message: String? = null,
    val isLoggedOut: Boolean = false
)