package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class User (
        @SerializedName("id")
        var id: Long,

        @SerializedName("name")
        var name: String,

        @SerializedName("email")
        var email: String
)