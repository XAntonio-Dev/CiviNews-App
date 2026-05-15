package com.example.civinews.data.models.user

import com.google.gson.annotations.SerializedName

data class NameUpdateRequest(
    @SerializedName("name") val name: String
)