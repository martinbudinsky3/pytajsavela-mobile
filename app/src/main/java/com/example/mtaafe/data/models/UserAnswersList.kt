package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class UserAnswersList(
    @SerializedName("id")
    var id: Long,

    @SerializedName("name")
    var name: String,

    @SerializedName("answers")
    var answers: ArrayList<UserAnswer>
)
