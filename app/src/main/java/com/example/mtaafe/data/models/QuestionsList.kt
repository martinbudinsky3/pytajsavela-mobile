package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class QuestionsList (
        @SerializedName("count")
        var count: Long,

        @SerializedName("questions")
        var questions: ArrayList<QuestionItem>
)