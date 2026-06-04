package com.example.civinews.ui.screens.audit

import com.example.civinews.data.models.user.UserResponse

sealed class UserAuditState {
    object Loading : UserAuditState()
    object NoData : UserAuditState()
    data class Error(val message: String) : UserAuditState()
    data class Success(val dataset: List<UserResponse>) : UserAuditState()
}