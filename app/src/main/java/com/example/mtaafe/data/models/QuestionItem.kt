package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class QuestionItem (
        @SerializedName("id")
        var id: Long,

        @SerializedName("title")
        var title: String,

        @SerializedName("created_at")
        var createdAt: LocalDate,

        @SerializedName("author")
        var author: Author,

        @SerializedName("tags")
        var tags: List<Tag>,

        @SerializedName("answers_count")
        var answersCount: Int
)