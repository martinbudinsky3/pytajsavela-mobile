package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Question (
        @SerializedName("id")
        var id: Long,

        @SerializedName("title")
        var title: String,

        @SerializedName("body")
        var body: String,

        @SerializedName("created_at")
        var createdAt: LocalDate,

        @SerializedName("author")
        var author: User,

        @SerializedName("tags")
        var tags: ArrayList<Tag>,

        @SerializedName("images")
        var images: ArrayList<Image>,

        @SerializedName("answers")
        var answers: ArrayList<AnswerItem>
)