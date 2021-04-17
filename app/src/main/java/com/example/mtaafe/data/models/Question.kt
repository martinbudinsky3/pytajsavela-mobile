package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class Question (
        @SerializedName("id")
        var id: Long,

        @SerializedName("title")
        var title: String,

        @SerializedName("body")
        var body: String,

        @SerializedName("tags")
        var tags: List<Tag>,

        @SerializedName("images")
        var images: List<Image>
)