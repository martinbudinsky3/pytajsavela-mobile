package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class LoggedInUser (
        @SerializedName("id")
        var id: Long,

        @SerializedName("api_token")
        var apiToken: String
)