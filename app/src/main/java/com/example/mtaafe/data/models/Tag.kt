package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class Tag (
        @SerializedName("id")
        var id: Long,

        @SerializedName("name")
        var name: String,

        @SerializedName("questions_count")
        var questionsCount: Int
) {

        override fun toString(): String {
                return name
        }
}