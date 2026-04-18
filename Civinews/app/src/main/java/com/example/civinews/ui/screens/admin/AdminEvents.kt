package com.example.civinews.ui.screens.admin


data class AdminEvents(
    val onApproveClick: (String) -> Unit,
    val onDeleteClick: (String) -> Unit,
    val onRefresh: () -> Unit
)