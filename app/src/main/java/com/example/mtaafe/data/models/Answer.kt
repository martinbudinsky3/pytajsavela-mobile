package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Answer (
        @SerializedName("id")
        var id: Long,

        @SerializedName("body")
        var body: String,

        @SerializedName("created_at")
        var createdAt: LocalDate,

        @SerializedName("author")
        var author: User,

        @SerializedName("images")
        var images: List<Image>
)

