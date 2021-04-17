package com.example.mtaafe.data.models

import com.google.gson.annotations.SerializedName

data class TagsList (
    @SerializedName("count")
    var count: Long,

    @SerializedName("tags")
    var tags: ArrayList<Tag>
)