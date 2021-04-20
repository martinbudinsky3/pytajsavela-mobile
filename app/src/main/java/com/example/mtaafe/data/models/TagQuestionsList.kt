package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class TagQuestionsList(
    @SerializedName("tag")
    var tag: Tag,

    @SerializedName("count")
    var count: Int,

    @SerializedName("questions")
    var questions: ArrayList<QuestionItem>
)
