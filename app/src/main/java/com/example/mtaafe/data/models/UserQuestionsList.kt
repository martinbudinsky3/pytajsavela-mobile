package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class UserQuestionsList(
    @SerializedName("id")
    var id: Long,

    @SerializedName("name")
    var name: String,

    @SerializedName("questions")
    var questions: ArrayList<QuestionItem>
)
