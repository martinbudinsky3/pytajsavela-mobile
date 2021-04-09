package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class Author (
        @SerializedName("id")
        var id: Long,

        @SerializedName("name")
        var name: String
)