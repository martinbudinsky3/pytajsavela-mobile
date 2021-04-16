package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class Image (
        @SerializedName("id")
        var id: Long,

        @SerializedName("file")
        var file: String
)