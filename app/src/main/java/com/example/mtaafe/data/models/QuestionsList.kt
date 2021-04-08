package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class QuestionsList (
        @SerializedName("count")
        var count: Int,

        @SerializedName("questions")
        var questions: List<QuestionItem>
)