package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class AnswersQuestion(
    @SerializedName("id")
    var id: Long,

    @SerializedName("title")
    var title: String
)
