package com.example.civinews.ui.screens.audit

data class UserAuditEvents(
    val onToggleRoleClick: (String) -> Unit,
    val onBanClick: (String) -> Unit,
    val onSearch: (String) -> Unit,
    val onRefresh: () -> Unit
)