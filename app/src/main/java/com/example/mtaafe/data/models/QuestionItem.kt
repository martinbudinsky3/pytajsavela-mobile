package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class QuestionItem (
    @SerializedName("id")
        var id: Long,

    @SerializedName("title")
        var title: String,

    @SerializedName("created_at")
        var createdAt: LocalDate,

    @SerializedName("author")
        var author: User,

    @SerializedName("tags")
        var tags: ArrayList<Tag>,

    @SerializedName("answers_count")
        var answersCount: Int
)