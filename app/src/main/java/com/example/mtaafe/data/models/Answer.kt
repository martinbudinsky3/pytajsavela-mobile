package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class Answer (
        @SerializedName("id")
        var id: Long,

        @SerializedName("body")
        var body: String,

        @SerializedName("images")
        var images: List<DecodedImage>
)