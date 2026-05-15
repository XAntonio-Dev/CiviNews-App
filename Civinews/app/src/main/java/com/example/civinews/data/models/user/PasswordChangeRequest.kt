package com.example.civinews.data.models.user

import com.google.gson.annotations.SerializedName

data class PasswordChangeRequest(
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String
)