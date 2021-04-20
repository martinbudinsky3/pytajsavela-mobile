package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class QuestionEdit (
    @SerializedName("id")
    var id: Long,

    @SerializedName("title")
    var title: String,

    @SerializedName("body")
    var body: String,

    @SerializedName("tags")
    var tags: List<Long>,

    @SerializedName("deleted_tags")
    var deleted_tags: List<Long>,
)