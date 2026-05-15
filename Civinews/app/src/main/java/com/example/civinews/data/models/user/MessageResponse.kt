package com.example.civinews.data.models.user

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("status") val status: String
)