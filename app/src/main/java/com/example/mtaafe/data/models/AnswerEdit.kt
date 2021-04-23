package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class AnswerEdit (
    @SerializedName("id")
    var id: Long,

    @SerializedName("body")
    var body: String,
)